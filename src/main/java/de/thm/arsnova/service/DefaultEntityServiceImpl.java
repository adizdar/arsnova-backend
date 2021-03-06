/*
 * This file is part of ARSnova Backend.
 * Copyright (C) 2012-2018 The ARSnova Team
 *
 * ARSnova Backend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ARSnova Backend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.thm.arsnova.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import de.thm.arsnova.event.AfterCreationEvent;
import de.thm.arsnova.event.AfterDeletionEvent;
import de.thm.arsnova.event.AfterFullUpdateEvent;
import de.thm.arsnova.event.AfterPatchEvent;
import de.thm.arsnova.event.BeforeCreationEvent;
import de.thm.arsnova.event.BeforeDeletionEvent;
import de.thm.arsnova.event.BeforeFullUpdateEvent;
import de.thm.arsnova.event.BeforePatchEvent;
import de.thm.arsnova.model.Entity;
import de.thm.arsnova.model.serialization.View;
import de.thm.arsnova.persistence.CrudRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Default implementation of {@link EntityService} which provides CRUD operations for entities independently from the
 * underlying persistence implementation. Authorization for entities is checked before any operation is performed.
 *
 * @param <T> Entity type
 * @author Daniel Gerhardt
 */
public class DefaultEntityServiceImpl<T extends Entity> implements EntityService<T>, ApplicationEventPublisherAware {
	protected Class<T> type;
	protected CrudRepository<T, String> repository;
	protected ApplicationEventPublisher eventPublisher;
	private ObjectMapper objectMapper;

	public DefaultEntityServiceImpl(Class<T> type, CrudRepository<T, String> repository, ObjectMapper objectMapper) {
		this.type = type;
		this.repository = repository;
		this.objectMapper = objectMapper;
	}

	@Override
	@PreAuthorize("hasPermission(#id, #this.this.getTypeName(), 'read')")
	public T get(final String id) {
		return repository.findOne(id);
	}

	@Override
	public T get(final String id, final boolean internal) {
		T entity;
		if (internal) {
			entity = repository.findOne(id);
			entity.setInternal(true);
		} else {
			entity = get(id);
		}
		modifyRetrieved(entity);

		return entity;
	}

	@Override
	@PreFilter(value = "hasPermission(filterObject, #this.this.getTypeName(), 'read')", filterTarget = "ids")
	public Iterable<T> get(final Iterable<String> ids) {
		Iterable<T> entities = repository.findAllById(ids);
		entities.forEach(this::modifyRetrieved);

		return entities;
	}

	@Override
	@PreAuthorize("hasPermission(#entity, 'create')")
	public T create(final T entity) {
		if (entity.getId() != null || entity.getRevision() != null) {
			throw new IllegalArgumentException("Entity is not new.");
		}
		entity.setCreationTimestamp(new Date());

		prepareCreate(entity);
		eventPublisher.publishEvent(new BeforeCreationEvent<>(this, entity));
		final T createdEntity = repository.save(entity);
		eventPublisher.publishEvent(new AfterCreationEvent<>(this, createdEntity));
		finalizeCreate(createdEntity);
		modifyRetrieved(entity);

		return createdEntity;
	}

	/**
	 * This method can be overridden by subclasses to modify the entity before creation.
	 *
	 * @param entity The entity to be created
	 */
	protected void prepareCreate(final T entity) {

	}

	/**
	 * This method can be overridden by subclasses to modify the entity after creation.
	 *
	 * @param entity The entity which has been created
	 */
	protected void finalizeCreate(final T entity) {

	}

	public T update(final T entity) {
		return update(repository.findOne(entity.getId()), entity);
	}

	@Override
	@PreAuthorize("hasPermission(#oldEntity, 'update')")
	public T update(final T oldEntity, final T newEntity) {
		newEntity.setId(oldEntity.getId());
		newEntity.setUpdateTimestamp(new Date());

		prepareUpdate(newEntity);
		eventPublisher.publishEvent(new BeforeFullUpdateEvent<>(this, newEntity, oldEntity));
		final T updatedEntity = repository.save(newEntity);
		eventPublisher.publishEvent(new AfterFullUpdateEvent<>(this, updatedEntity, oldEntity));
		finalizeUpdate(updatedEntity);
		modifyRetrieved(updatedEntity);

		return updatedEntity;
	}

	/**
	 * This method can be overridden by subclasses to modify the entity before updating.
	 *
	 * @param entity The entity to be updated
	 */
	protected void prepareUpdate(final T entity) {

	}

	/**
	 * This method can be overridden by subclasses to modify the entity after updating.
	 *
	 * @param entity The entity which has been updated
	 */
	protected void finalizeUpdate(final T entity) {

	}

	@Override
	public T patch(final T entity, final Map<String, Object> changes) throws IOException {
		return patch(entity, changes, Function.identity());
	}

	@Override
	@PreAuthorize("hasPermission(#entity, 'update')")
	public T patch(final T entity, final Map<String, Object> changes,
			final Function<T, ? extends Object> propertyGetter) throws IOException {
		Object obj = propertyGetter.apply(entity);
		ObjectReader reader = objectMapper.readerForUpdating(obj).withView(View.Public.class);
		JsonNode tree = objectMapper.valueToTree(changes);
		reader.readValue(tree);
		entity.setUpdateTimestamp(new Date());
		preparePatch(entity);
		eventPublisher.publishEvent(new BeforePatchEvent<>(this, entity, propertyGetter, changes));
		final T patchedEntity = repository.save(entity);
		eventPublisher.publishEvent(new AfterPatchEvent<>(this, patchedEntity, propertyGetter, changes));
		modifyRetrieved(patchedEntity);

		return patchedEntity;
	}

	@Override
	public Iterable<T> patch(final Iterable<T> entities, final Map<String, Object> changes) throws IOException {
		return patch(entities, changes, Function.identity());
	}

	@Override
	@PreFilter(value = "hasPermission(filterObject, 'update')", filterTarget = "entities")
	public Iterable<T> patch(final Iterable<T> entities, final Map<String, Object> changes,
			final Function<T, ? extends Object> propertyGetter) throws IOException {
		final JsonNode tree = objectMapper.valueToTree(changes);
		for (T entity : entities) {
			Object obj = propertyGetter.apply(entity);
			ObjectReader reader = objectMapper.readerForUpdating(obj).withView(View.Public.class);
			reader.readValue(tree);
			entity.setUpdateTimestamp(new Date());
			preparePatch(entity);
			eventPublisher.publishEvent(new BeforePatchEvent<>(this, entity, propertyGetter, changes));
		}

		Iterable<T> patchedEntities = repository.saveAll(entities);
		patchedEntities.forEach((e) -> {
			eventPublisher.publishEvent(new AfterPatchEvent<>(this, e, propertyGetter, changes));
			modifyRetrieved(e);
		});

		return patchedEntities;
	}

	/**
	 * This method can be overridden by subclasses to modify the entity before patching. By default, the implementation
	 * of {@link #prepareUpdate} is used.
	 *
	 * @param entity The entity to be patched
	 */
	protected void preparePatch(final T entity) {
		prepareUpdate(entity);
	}

	@Override
	@PreAuthorize("hasPermission(#entity, 'delete')")
	public void delete(final T entity) {
		prepareDelete(entity);
		eventPublisher.publishEvent(new BeforeDeletionEvent<>(this, entity));
		repository.delete(entity);
		eventPublisher.publishEvent(new AfterDeletionEvent<>(this, entity));
	}

	/**
	 * This method can be overridden by subclasses to do additional entity related actions before deletion.
	 *
	 * @param entity The entity to be deleted
	 */
	protected void prepareDelete(final T entity) {

	}

	/**
	 * This method can be overridden by subclasses to modify a retrieved entity before it is returned by service
	 * methods.
	 *
	 * @param entity The entity to be modified
	 */
	protected void modifyRetrieved(final T entity) {

	}

	public String getTypeName() {
		return type.getSimpleName().toLowerCase();
	}

	@Override
	public void setApplicationEventPublisher(final ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}
}
