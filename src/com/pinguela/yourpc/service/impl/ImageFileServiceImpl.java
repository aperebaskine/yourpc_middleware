package com.pinguela.yourpc.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.ServiceException;
import com.pinguela.yourpc.config.ConfigManager;
import com.pinguela.yourpc.model.ImageEntry;
import com.pinguela.yourpc.service.ImageFileService;

public class ImageFileServiceImpl 
implements ImageFileService {

	private static Logger logger = LogManager.getLogger(ImageFileServiceImpl.class);

	private static final String IMAGE_DIRECTORY_PNAME = "service.image.directory";
	private static final String IMAGE_DIRECTORY = ConfigManager.getValue(IMAGE_DIRECTORY_PNAME);
	private static final String EXTENSION = "png";

	@Override
	public List<ImageEntry> getFiles(String type, Serializable pk) throws ServiceException {
		List<ImageEntry> fileList = new ArrayList<>();
		File[] imageFiles = getFileList(type, pk);
		
		if (imageFiles == null) {
			return fileList;
		}
		
		for (File file : imageFiles) {
			try {
				fileList.add(new ImageEntry(ImageIO.read(file), file.getPath()));
			} catch (IOException e) {
				logger.error(String.format("Failed to retrieve image file: %s", e.getMessage()), e);
				throw new ServiceException(e.getMessage(), e);
			}
		}

		return fileList;
	}

	@Override
	public Integer add(String type, Serializable pk, ImageEntry imageFile) throws ServiceException {
		return add(type, pk, Arrays.asList(imageFile));
	}

	private Integer add(String type, Serializable pk, List<ImageEntry> imageFiles) throws ServiceException {

		File directory = getDirectory(type, pk);
		File[] storedFiles = directory.listFiles();
		Integer nextFileName = Integer.valueOf(getLastFileIndex(storedFiles) + 1);

		for (ImageEntry imageFile : imageFiles) {
			String path = imageFile.getPath() != null 
					? imageFile.getPath() // Overwrite the existing file
							: new StringBuilder(directory.getPath())
							.append(File.separator).append(nextFileName++)
							.append('.').append(EXTENSION).toString();
			File outputFile = new File(path);
			
			try {
				ImageIO.write(imageFile.getImage(), EXTENSION, outputFile);
				logger.info(String.format("Image stored in path %s", path));
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new ServiceException(e.getMessage(), e);
			} 
		}
		return nextFileName;
	}

	@Override
	public Boolean update(String type, Serializable pk, List<ImageEntry> imageFiles) throws ServiceException {

		List<ImageEntry> storedFilePaths = wrapPathNames(type, pk);
		storedFilePaths.removeAll(imageFiles);

		for (ImageEntry forDelete : storedFilePaths) {
			delete(forDelete.getPath());
		}

		add(type, pk, imageFiles);

		return null;
	}

	@Override
	public Boolean delete(String filePath) throws ServiceException {
		if (!filePath.startsWith(IMAGE_DIRECTORY)) {
			throw new IllegalArgumentException(String.format("Image directory does not contain the file path %s.", filePath));
		}

		File file = new File(filePath);
		return file.delete();
	}

	private File getDirectory(String type, Serializable pk) {

		String path = new StringBuilder(IMAGE_DIRECTORY)
				.append(File.separator)
				.append(type)
				.append(File.separator)
				.append(pk)
				.toString();
		
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
		return file;
	}

	private File[] getFileList(String type, Serializable pk) {
		File directory = getDirectory(type, pk);
		File[] imageFiles = directory.listFiles();
		return imageFiles;
	}

	private List<ImageEntry> wrapPathNames(String type, Serializable pk) {
		List<ImageEntry> list = new ArrayList<>();
		File[] files = getFileList(type, pk);

		for (File file : files) {
			list.add(new ImageEntry(file.getPath()));
		}
		return list;
	}

	private int getLastFileIndex(File[] files) {

		int lastIndex = 0;

		for (File f : files) {
			String name = f.getName().substring(0, f.getName().indexOf('.'));
			int index = Integer.valueOf(name);
			if (index > lastIndex) {
				lastIndex = index;
			}
		}
		return lastIndex;
	}

}