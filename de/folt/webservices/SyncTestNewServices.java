package de.folt.webservices;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class SyncTestNewServices
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		SynchronizeService synchronizeService = new SynchronizeService();

		String dataSourceName = "anewsyncdatabase";
		HashMap<String, String> paramHash = new HashMap<String, String>();
		paramHash.put("name", dataSourceName);
		
		paramHash.put("user-id", "Klemens");

		paramHash = synchronizeService.listUser(paramHash);
		print("listUser", paramHash);

		paramHash = synchronizeService.getCreator(paramHash);
		print("getCreator", paramHash);

		paramHash.put("newuser", "MaxMustermann");
		paramHash = synchronizeService.addUser(paramHash);
		print("addUser", paramHash);
		paramHash.remove("newuser");
		
		paramHash.put("newuser", "Michael");
		paramHash = synchronizeService.addUser(paramHash);
		print("addUser", paramHash);
		paramHash.remove("newuser");
		
		paramHash.put("newuser", "Stefan");
		paramHash = synchronizeService.addUser(paramHash);
		print("addUser", paramHash);
		paramHash.remove("newuser");

		paramHash = synchronizeService.listUser(paramHash);
		print("listUser", paramHash);

		paramHash.put("removeuser", "MaxMustermann");
		paramHash = synchronizeService.removeUser(paramHash);
		print("removeuser", paramHash);
		paramHash.remove("removeuser");
		
		paramHash.put("removeuser", "hirni");
		paramHash = synchronizeService.removeUser(paramHash);
		print("removeuser", paramHash);
		paramHash.remove("removeuser");

		paramHash = synchronizeService.listUser(paramHash);
		print("listUser", paramHash);
		
		paramHash.put("newuser", "wxyz");
		paramHash = synchronizeService.addUser(paramHash);
		print("addUser", paramHash);
		paramHash.remove("newuser");

		paramHash.put("newuser", "xyz");
		paramHash = synchronizeService.addUser(paramHash);
		print("addUser", paramHash);
		paramHash.remove("newuser");
	
		paramHash.put("newuser", "anyother");
		paramHash = synchronizeService.addUser(paramHash);
		print("addUser", paramHash);
		paramHash.remove("newuser");
		
		paramHash = synchronizeService.listUser(paramHash);
		print("listUser", paramHash);
		
		paramHash.put("removeuser", "xyz");
		paramHash = synchronizeService.removeUser(paramHash);
		print("removeuser", paramHash);
		paramHash.remove("removeuser");
		
		paramHash = synchronizeService.listUser(paramHash);
		print("listUser", paramHash);
		
		dataSourceName = "myadditionalsynctest";
		paramHash = new HashMap<String, String>();
		paramHash.put("name", dataSourceName);

		paramHash = synchronizeService.getCreator(paramHash);
		print("getCreator", paramHash);
		
		paramHash = new HashMap<String, String>();
		paramHash.put("name", "M002");
		
		System.out.println("\n\n---- Database creation test");
	
		paramHash.put("dataSourceName", "M002"); 
		paramHash.put("dataSourceType", "C:/Program Files/OpenTMS/hibernate/hibernate.mssqlserver.cfg.xml"); 
		paramHash.put("hibernateConfigFile", "C:/Program Files/OpenTMS/hibernate/hibernate.mssqlserver.cfg.xml"); 
		paramHash.put("dataSourceServer", "localhost"); 
		paramHash.put("dataSourcePort", "1433"); 
		paramHash.put("dataSourceUser", "Araya"); 
		paramHash.put("dataSourcePassword", "araya"); 
		paramHash.put("user-id", "Klemens");
		paramHash.put("sync-user-id-list", "Klemens");
		
		paramHash = synchronizeService.delete(paramHash);
		print("delete", paramHash);
		
		paramHash = synchronizeService.create(paramHash);
		print("create", paramHash);
		
		paramHash = new HashMap<String, String>();
		paramHash.put("name", "M002");
		paramHash.put("dataSourceType", "C:/Program Files/OpenTMS/hibernate/hibernate.mssqlserver.cfg.xml"); // MySQL
		paramHash.put("hibernateConfigFile", "C:/Program Files/OpenTMS/hibernate/hibernate.mssqlserver.cfg.xml"); // MySQL
		paramHash.put("user-id", "Klemens");
		
		paramHash.put("newuser", "xyz");
		paramHash = synchronizeService.addUser(paramHash);
		print("addUser", paramHash);
		
		paramHash.put("newuser", "wxyz");
		paramHash = synchronizeService.addUser(paramHash);
		print("addUser", paramHash);
		
		paramHash.put("newuser", "anyother");
		paramHash = synchronizeService.addUser(paramHash);
		print("addUser", paramHash);
		
		paramHash = synchronizeService.listUser(paramHash);
		print("listUser", paramHash);
		
		paramHash = new HashMap<String, String>();
		paramHash.put("name", "M002");
		paramHash.put("dataSourceType", "C:/Program Files/OpenTMS/hibernate/hibernate.mssqlserver.cfg.xml"); // MySQL
		paramHash.put("hibernateConfigFile", "C:/Program Files/OpenTMS/hibernate/hibernate.mssqlserver.cfg.xml"); // MySQL
		paramHash.put("user-id", "Klemens");
		
		paramHash = synchronizeService.clean(paramHash);
		print("clean", paramHash);
		
		paramHash = synchronizeService.delete(paramHash);
		print("delete", paramHash);
		
		System.out.println("\n\n---- 2nd Database creation test");
		
		paramHash.put("dataSourceName", "M002"); 
		paramHash.put("dataSourceType", "C:/Program Files/OpenTMS/hibernate/hibernate.mssqlserver.cfg.xml"); 
		paramHash.put("hibernateConfigFile", "C:/Program Files/OpenTMS/hibernate/hibernate.mssqlserver.cfg.xml"); 
		paramHash.put("dataSourceServer", "localhost"); 
		paramHash.put("dataSourcePort", "1433"); 
		paramHash.put("dataSourceUser", "Araya"); 
		paramHash.put("dataSourcePassword", "araya"); 
		paramHash.put("user-id", "Klemens");
		paramHash.put("sync-user-id-list", "Klemens");
		
		paramHash.put("newuser", "kim");
		paramHash = synchronizeService.addUser(paramHash);
		print("addUser", paramHash);
		
		paramHash = synchronizeService.listUser(paramHash);
		print("listUser", paramHash);
		
		paramHash = synchronizeService.delete(paramHash);
		print("delete", paramHash);
	
	}

	public static void print(String message, HashMap<String, String> paramHash)
	{
		System.out.println("------------------------------ Start " + message);
		Set<String> enum1 = paramHash.keySet();
		Iterator<String> it1 = enum1.iterator();
		System.out.println("Message: " + message);
		while (it1.hasNext())
		{
			String key = it1.next();
			String value = paramHash.get(key);
			System.out.println("\t" + key + "=" + value);
		}
		System.out.println("------------------------------- End " + message);
	}

}
