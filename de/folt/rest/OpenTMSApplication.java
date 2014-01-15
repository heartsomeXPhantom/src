package de.folt.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class OpenTMSApplication extends Application
{

	@Override
	public Set<Class<?>> getClasses()
	{
		// TODO Auto-generated method stub
		HashSet<Class<?>> set = new HashSet<Class<?>>();
		set.add(OpenTMSResource.class);
		return super.getClasses();
	}

	@Override
	public Set<Object> getSingletons()
	{
		// TODO Auto-generated method stub
		return super.getSingletons();
	}

}
