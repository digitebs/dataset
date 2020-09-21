package org.examples.model;

import java.util.ArrayList;
import java.util.function.Function;

public class Folder {
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public Folder(String name, long size) {
    this.name = name;
    this.size = size;
  }

  private long size;


  public static Function<String, ArrayList<Folder>> toFolder = (s) -> {
    String[] strs = s.split(",");

    ArrayList<Folder> list = new ArrayList<>();
    for (int i = 0; i < strs.length; i += 2) {
      list.add(new Folder(strs[i], Long.parseLong(strs[i + 1])));
    }

    return list;
  };

}
