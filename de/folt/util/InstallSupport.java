/*
 * Created on 14.07.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Vector;

import com.araya.eaglememex.util.LogPrint;

/**
 * Class contains some support methods for the installation process. Mainly replacing the base openTMS directory
 * @author klemens
 *
 */
public class InstallSupport
{

    private String installFiles = "replace.cmd";

    private String OPENTMSPATHDIRECTORY = "OPENTMSPATHDIRECTORY";

    /**
     * adaptInstallationFiles adapts some files immediately after an installation.<br>
     * First it is checked if in the current directory a file names "installed" exists. If yes, the method returns true (no changes have been necessary)
     * Otherwise the variable installFiles (e.g. "replace.cmd") is read. This file contains a list of files which should be changed.<br>Example replace.cmd file:<pre>
     * OpenTMS.xml
     * lib/OpenTMS.properties<pre>
     * In these files all occurrences of OPENTMSPATHDIRECTORY will be replaced with the current directory name
     * 
     * @return true for success, otherwise false
     */
    public boolean adaptInstallationFiles()
    {
        try
        {
            String installDirectory = de.folt.util.OpenTMSSupportFunctions.getCurrentDirectory();
            installDirectory = installDirectory.replaceAll("\\\\", "/");
            // first - check if install file exists
            String installfile = installDirectory + "/" + "installed";
            File installed = new File(installfile);
            if (!installed.exists())
                return true; // ok - no changes necessary
            // second - get the name of the installation directory
            String fileListName = installDirectory + "/" + installFiles; // "replace.cmd";
            // third run adaptations
            // 1) adapt the filenames in fileListName
            readReplace(fileListName, this.getOPENTMSPATHDIRECTORY(), installDirectory);
            // 2) run adaptations for those files
            // call %CALLING% replace.cmd "getOPENTMSPATHDIRECTORY" "%ARAYAPATH%=installdirectory"
            readReplaceFromFile(fileListName, this.getOPENTMSPATHDIRECTORY(), installDirectory);
            de.folt.util.OpenTMSSupportFunctions.copyFile(installfile, installfile + ".bak");
            installed.delete();

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * @return the installFiles
     */
    public String getInstallFiles()
    {
        return installFiles;
    }

    /**
     * @return the oPENTMSPATHDIRECTORY
     */
    public String getOPENTMSPATHDIRECTORY()
    {
        return OPENTMSPATHDIRECTORY;
    }

    /**
     * readReplace reads a file and replaces all occurrences of oldPattern with replPattern. The original file is backed up to <filename>.bak
     * 
     * @param filename the file name to adapt
     * @param oldPattern the pattern to change
     * @param replPattern the pattern which is used for replacement
     */
    public void readReplace(String filename, String oldPattern, String replPattern)
    {
        String line;
        StringBuffer sb = new StringBuffer();
        int iLines = 0;
        int iChanges = 0;

        // first make a copy of the original file
        String keepOrigFile = filename + ".bak";

        String oldfilename = filename;
        String replfile = filename.replaceAll(oldPattern, replPattern); // replace the file name
        if (!replfile.equals(filename))
            filename = replfile;

        System.out.println("Filename: " + filename + " (" + oldfilename + ") oldPattern=" + oldPattern + " replPattern=" + replPattern + "\n");
        try
        {
            @SuppressWarnings("unused")
            boolean bCopied = de.folt.util.OpenTMSSupportFunctions.copyFile(filename, keepOrigFile);
            FileInputStream fis = new FileInputStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            while ((line = reader.readLine()) != null)
            {
                String oldline = line;
                iLines++;
                line = line.replaceAll(oldPattern, replPattern);
                if (!line.equals(oldline))
                    iChanges++;
                sb.append(line + "\n");
                oldline = null;
            }
            reader.close();
            if (iChanges > 0)
            {
                BufferedWriter out = new BufferedWriter(new FileWriter(filename));
                out.write(sb.toString());
                out.close();
            }

            LogPrint.printPure("File: " + filename + " Lines: " + iLines + " Changes: " + iChanges + "\n");

        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * readReplace do readReplace on a set of files given in a string array
     * 
     * @param filename the array of strings with the filenames to replace
     * @param oldPattern the pattern to change
     * @param replPattern the pattern which is used for replacement
     */
    public void readReplace(String[] filename, String oldPattern, String replPattern)
    {
        for (int i = 0; i < filename.length; i++)
        {
            readReplace(filename[i], oldPattern, replPattern);
        }
    }

    /**
     * readReplace
     * 
     * @param filename do readReplace on a set of files given in a Vector of strings
     * @param oldPattern the pattern to change
     * @param replPattern the pattern which is used for replacement
     */
    public void readReplace(Vector<String> filename, String oldPattern, String replPattern)
    {
        for (int i = 0; i < filename.size(); i++)
        {
            readReplace((String) filename.get(i), oldPattern, replPattern);
        }
    }

    /**
     * readReplaceFromFile
     * 
     * @param fname
     * @param oldPattern
     * @param replPattern
     */
    public void readReplaceFromFile(String fname, String oldPattern, String replPattern)
    {
        String line;
        Vector<String> filevec = new Vector<String>();
        try
        {
            FileInputStream fis = new FileInputStream(fname);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            while ((line = reader.readLine()) != null)
            {
                if (line.indexOf("*") > -1)
                {
                    // get the file list
                    // first get the directory and then selecting file
                    String directoryName = "";
                    if (line.indexOf("/") > -1)
                    {
                        directoryName = line.substring(0, line.indexOf("/"));
                    }
                    else if (line.indexOf("\\") > -1)
                    {
                        directoryName = line.substring(0, line.indexOf("\\"));
                    }
                    else
                        directoryName = ".";
                    File dir = new File(directoryName);
                    String[] children = dir.list();
                    if (children == null)
                    {
                        // Either dir does not exist or is not a directory
                    }
                    else
                    {
                        for (int i = 0; i < children.length; i++)
                        {
                            // Get filename of file or directory
                            String filename = children[i];
                            if (filename.matches(line))
                            {
                                filevec.add(filename);
                            }
                        }
                    }
                }
                else
                    filevec.add(line);
            }
            reader.close();
            readReplace(filevec, oldPattern, replPattern);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @param installFiles the installFiles to set
     */
    public void setInstallFiles(String installFiles)
    {
        this.installFiles = installFiles;
    }
    
    /**
     * @param opentmspathdirectory the oPENTMSPATHDIRECTORY to set
     */
    public void setOPENTMSPATHDIRECTORY(String opentmspathdirectory)
    {
        OPENTMSPATHDIRECTORY = opentmspathdirectory;
    }
}
