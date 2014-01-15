/*
 * Created on 31.10.2007
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.araya.eaglememex.util.LogPrint;

public class ZipArchive
{
    /**
     * main
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        ZipArchive.extractArchive(new File(args[0]), new File(args[1]));
    }

    /**
     * getArchiveFileNames
     * 
     * @param archive
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Vector<String> getArchiveFileNames(File archive) throws Exception
    {
        Vector<String> files = new Vector<String>();

        ZipFile zipFile = new ZipFile(archive);
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipFile.entries();

        while (entries.hasMoreElements())
        {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            String entryFileName = entry.getName();
            files.add(entryFileName);
        }

        return files;
    }

    /**
     * getArchiveFileNames
     * 
     * @param archiveName
     * @return
     * @throws Exception
     */
    public static Vector<String> getArchiveFileNames(String archiveName) throws Exception
    {
        File archive = new File(archiveName);
        return getArchiveFileNames(archive);
    }

    /**
     * isArchive
     * 
     * @param archive
     * @return
     */
    public static boolean isArchive(File archive)
    {

        try
        {
            @SuppressWarnings("unused")
            ZipFile zipFile = new ZipFile(archive);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * isArchive
     * 
     * @param archive
     * @return
     */
    public static boolean isArchive(String archiveName)
    {
        try
        {
            File archive = new File(archiveName);
            @SuppressWarnings("unused")
            ZipFile zipFile = new ZipFile(archive);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * extractArchive
     * 
     * @param archiveFile
     * @param destDirName
     * @throws Exception
     */
    public static void extractArchive(String archiveFile, String destDirName) throws Exception
    {
        File archive = new File(archiveFile);
        File destDir = new File(destDirName);
        extractArchive(archive, destDir);
    }

    /**
     * extractArchive
     * 
     * @param archive
     * @param inArchive
     * @param destDir
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void extractArchive(File archive, String inArchive, File destDir) throws Exception
    {
        if (!destDir.exists())
        {
            destDir.mkdir();
        }

        ZipFile zipFile = new ZipFile(archive);
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipFile.entries();

        byte[] buffer = new byte[16384];
        int len;
        while (entries.hasMoreElements())
        {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            String entryFileName = entry.getName();

            if (!entryFileName.equals(inArchive))
                continue;

            File dir = buildDirectoryHierarchyFor(entryFileName, destDir);
            if (!dir.exists())
            {
                dir.mkdirs();
            }

            if (!entry.isDirectory())
            {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(destDir, entryFileName)));

                BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));

                while ((len = bis.read(buffer)) > 0)
                {
                    bos.write(buffer, 0, len);
                }

                bos.flush();
                bos.close();
                bis.close();
            }
        }
    }

    /**
     * extractArchive
     * 
     * @param archive
     * @param inArchive
     * @param outfile
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void extractArchive(File archive, String inArchive, String outfile) throws Exception
    {

        ZipFile zipFile = new ZipFile(archive);
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipFile.entries();

        byte[] buffer = new byte[16384];
        int len;
        while (entries.hasMoreElements())
        {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            String entryFileName = entry.getName();
            entryFileName.replaceAll("\\\\", "/");

            LogPrint.println("entryFileName: \"" + entryFileName + "\" inArchive: \"" + inArchive + "\"");
            if (!entryFileName.equals(inArchive))
            {
                continue;
            }
            else
            {

                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(outfile)));

                BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));

                while ((len = bis.read(buffer)) > 0)
                {
                    bos.write(buffer, 0, len);
                }

                bos.flush();
                bos.close();
                bis.close();
                break;
            }
        }
    }

    /**
     * extractArchive
     * 
     * @param archive
     * @param destDir
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void extractArchive(File archive, File destDir) throws Exception
    {
        if (!destDir.exists())
        {
            destDir.mkdir();
        }

        ZipFile zipFile = new ZipFile(archive);
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipFile.entries();

        byte[] buffer = new byte[16384];
        int len;
        while (entries.hasMoreElements())
        {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            String entryFileName = entry.getName();

            File dir = buildDirectoryHierarchyFor(entryFileName, destDir);
            if (!dir.exists())
            {
                dir.mkdirs();
            }

            if (!entry.isDirectory())
            {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(destDir, entryFileName)));

                BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));

                while ((len = bis.read(buffer)) > 0)
                {
                    bos.write(buffer, 0, len);
                }

                bos.flush();
                bos.close();
                bis.close();
            }
        }
    }

    /**
     * buildDirectoryHierarchyFor
     * 
     * @param entryName
     * @param destDir
     * @return
     */
    private static File buildDirectoryHierarchyFor(String entryName, File destDir)
    {
        int lastIndex = entryName.lastIndexOf('/');
        String internalPathToEntry = entryName.substring(0, lastIndex + 1);
        return new File(destDir, internalPathToEntry);
    }

    /**
     * replaceFile
     * 
     * @param archive
     * @param toReplace
     * @param newReplace
     */
    public static boolean replaceFile(String archive, String toReplace, String newReplace)
    {
        try
        {
            String tempFile = archive + ".copy";
            com.araya.utilities.FileTextReplacer.copyFile(archive, tempFile);

            byte[] buf = new byte[1024];

            ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archive));
            ZipEntry entry = zin.getNextEntry();
            while (entry != null)
            {
                String name = entry.getName();
                name = name.replaceAll("\\\\", "/");
                if (toReplace.equals(name)) // ignore the old file
                {
                    ;
                }
                else
                {
                    // Add ZIP entry to output stream.
                    out.putNextEntry(new ZipEntry(name));
                    // Transfer bytes from the ZIP file to the output file
                    int len;
                    while ((len = zin.read(buf)) > 0)
                    {
                        out.write(buf, 0, len);
                    }
                }
                entry = zin.getNextEntry();
            }
            // Close the streams
            zin.close();
            // Compress the files

            InputStream in = new FileInputStream(newReplace);
            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(toReplace));
            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }
            // Complete the entry
            out.closeEntry();
            in.close();

            // Complete the ZIP file
            out.close();
            File temp = new File(tempFile);
            temp.delete();
            return true;

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
}
