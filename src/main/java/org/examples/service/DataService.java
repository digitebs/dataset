package org.examples.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import org.examples.model.Folder;

public interface DataService {
  void generateFiles(List<Folder> folders) ;
}
