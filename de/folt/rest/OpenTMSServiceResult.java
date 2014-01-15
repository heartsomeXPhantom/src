package de.folt.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OpenTMSServiceResult
{
	private String message;
	private Integer returncode;
	private List<?> ergebnisse;

	public Integer getReturncode()
	{
		return returncode;
	}

	public void setReturncode(Integer returncode)
	{
		this.returncode = returncode;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public List<?> getErgebnisse()
	{
		return ergebnisse;
	}

	public void setErgebnisse(List<?> ergebnisse)
	{
		this.ergebnisse = ergebnisse;
	}
}