package org.examples;

import static org.examples.model.Folder.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.LogManager;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.examples.model.Folder;
import org.examples.service.DataService;
import org.examples.service.DataServiceImpl;

public class Main {
  public static void main(String[] args) {

    List<String> arrays = Arrays.stream(args).collect(Collectors.toList());
    if (arrays.size() == 2) arrays.add(1, "0");


    final String dir = arrays.get(0);
    final int size = Integer.parseInt(arrays.get(1));
    final ArrayList<Folder> folders = toFolder.apply(arrays.get(2));
    try {
      final DataService ds = new DataServiceImpl(dir, size);
      ds.generateFiles(folders);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
