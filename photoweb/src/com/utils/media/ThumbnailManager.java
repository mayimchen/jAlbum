package com.utils.media;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backend.FileInfo;
import com.utils.conf.AppConfig;

public class ThumbnailManager
{
    private static final Logger logger = LoggerFactory.getLogger(ThumbnailManager.class);

    private static boolean isBaseDriValid = false;

    private static String baseDir = null;

    static
    {
        try
        {
            File f = new File(AppConfig.getInstance().getThumbnailDir());
            if (!f.exists())
            {
                f.mkdirs();
            }
            else
            {
                if (f.isFile())
                {
                    System.out.println("the thumbnail dir is not empty.");
                }
            }

            if (f.isDirectory())
            {
                baseDir = f.getCanonicalPath();
                isBaseDriValid = true;
            }
        }
        catch (IOException e)
        {
            logger.warn("the input thumbnail dir path is invalid.");
        }
    }

    private static String getPicThumbnailPath(String id)
    {
        String dir2 = id.substring(id.length() - 2, id.length());
        String dir1 = id.substring(id.length() - 4, id.length() - 2);
        return baseDir + File.separator + dir1 + File.separator + dir2 + File.separator + id;
    }

    public static boolean checkTheThumbnailExist(String id)
    {
        if (!isBaseDriValid || StringUtils.isBlank(id) || id.length() < 4)
        {
            logger.warn("the pic file id is invalid.");
            return false;
        }

        return new File(getPicThumbnailPath(id)).isFile();
    }

    public static void checkAndGenThumbnail(FileInfo fi)
    {
        if (fi == null)
        {
            return;
        }

        String id = fi.getHash256();
        if (!isBaseDriValid || StringUtils.isBlank(id) || id.length() < 4)
        {
            logger.warn("the pic file id is invalid.");
            return;
        }

        if (checkTheThumbnailExist(id))
        {
            logger.debug("the thumbnail of pic ({}) is alread exist.", id);
            return;
        }

        logger.warn("now try to gen the thumbnail for: {}", fi);
        File thumbnailFile = new File(getPicThumbnailPath(id));
        File parentDir = thumbnailFile.getParentFile();
        if (!parentDir.exists())
        {
            parentDir.mkdirs();
        }

        String tmpFile = "." + File.separator + fi.getHash256();
        File tmpF = new File(tmpFile);
        ThumbnailGenerator.createThumbnail(fi, tmpFile, 400, 400, false);

        if (!tmpF.renameTo(new File(getPicThumbnailPath(id))))
        {
            logger.warn("generate the Thumbnail file failed!");
        }
    }

    public static File getThumbnail(String id)
    {
        if (!isBaseDriValid)
        {
            return null;
        }

        String path = getPicThumbnailPath(id);
        File f = new File(path);
        if (f.isFile())
        {
            return f;
        }

        return null;
    }
}
