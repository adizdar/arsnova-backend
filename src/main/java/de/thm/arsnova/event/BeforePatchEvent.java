package de.thm.arsnova.event;

import de.thm.arsnova.model.Entity;

public class BeforePatchEvent<E extends Entity> extends BeforeUpdateEvent<E> {
	public BeforePatchEvent(final E source) {
		super(source);
	}
}
