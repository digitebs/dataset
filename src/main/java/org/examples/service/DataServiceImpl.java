package org.examples.service;

import static java.nio.file.StandardOpenOption.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.ToLongFunction;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.examples.model.Folder;

public class DataServiceImpl implements DataService {

  private static Logger logger = Logger.getLogger(DataService.class.getName());

  private static final long MB = (long) 1e6;
  private String dir;
  private long size;

  public DataServiceImpl(String dir) throws Exception {
    this(dir, 0L);
  }

  public DataServiceImpl(String dir, long size) throws Exception {
    this.dir = dir;
    this.size =
        writeOrGetMeta
            .apply(dir, size)
            .orElseThrow(() -> new Exception("unable to determine size.")) * MB;
  }

  public final ToLongFunction<Path> pathSize =
      (name) -> {
        try (Stream<Path> stream = Files.walk(name)) {
          return stream
              .parallel()
              .filter(Files::isRegularFile)
              .map(p -> p.toFile())
              .mapToLong(p -> p.length())
              .sum();
        } catch (IOException ioe) {
          return -1;
        }
      };

  private BiFunction<String, Long, Optional<Long>> writeOrGetMeta =
      (dir, size) -> {
        Path path = Paths.get(dir);
        try {
          Files.createDirectories(path);
          Path meta = Paths.get(dir + "/.meta");
          if (Files.exists(meta)) {
            return Files.lines(meta).findFirst().map(Long::parseLong);
          } else {
            Files.write(meta, size.toString().getBytes(), CREATE_NEW);
          }
        } catch (IOException ioe) {
          logger.warning("Exception occred reading size.");
        }
        return Optional.of(size).filter(s -> s != 0);
      };

  public void generateFiles(List<Folder> folders) {
    folders
        .parallelStream()
        .forEach(
            f -> {
              Path name = Paths.get(dir + "/" + f.getName());
              try {
                Files.createDirectories(name);
                long fc =
                    Optional.of(fileCount.applyAsLong(name))
                        .filter(c -> c != -1)
                        .map(c -> c == 0 ? c : c - 1)
                        .orElseThrow(() -> new Exception("Unable to determine file count."));

                long currSize = Optional.of(pathSize.applyAsLong(name))
                    .filter(c -> c != -1)
                    .orElseThrow(() -> new Exception("Unable to determine path size."));
                long totalSize = Math.round((double)(currSize + f.getSize()*MB)/MB) * MB;

                logger.info(
                    String.format(
                        "%s last: %s, cap: %d, curr: %d, total: %d",
                        name, fc, size, currSize, totalSize));
                while (totalSize - currSize > MAX_LENGTH ) {
                  Path data = Paths.get(name + "/" + fc + ".data");
                  long rem =
                      Math.min((totalSize - currSize), size - data.toFile().length());

                  if (rem <= MAX_LENGTH) {
                    fc++; // move to next file
                    continue;
                  }

                  currSize += writePath(data, rem);
                  logger.info(
                      String.format(
                          "%s mb written: %d, filename:: %d.data file size: %d",
                          name, currSize, fc, data.toFile().length()));
                }
              } catch (Exception e) {
                e.printStackTrace();
              }
            });
  }

  private static final int MAX_LENGTH = 40;
  private static final int MIN_LENGTH = 1;

  private static final String CONTENTS = "abcdefghijklmnopqrstuvwxyz0123456789";

  private final Random r = new Random();

  private double writePath(Path data, long length) throws IOException {

    double m = 0;
    try (BufferedWriter bw = Files.newBufferedWriter(data, CREATE, APPEND)) {
      while (m + MAX_LENGTH < length) {

        int len = MIN_LENGTH + r.nextInt(MAX_LENGTH);
        final String c =
            IntStream.generate(() -> r.nextInt(CONTENTS.length() - 1))
                    .mapToObj(x -> String.valueOf(CONTENTS.charAt(x)))
                    .limit(len)
                    .collect(Collectors.joining())
                + "\n";
        bw.write(c);
        m += c.getBytes().length;
      }
    }
    return m; // bytes written
  }

  public final ToLongFunction<Path> fileCount =
      p -> {
        try (Stream<Path> stream = Files.walk(p)) {
          return stream.parallel().filter(Files::isRegularFile).count();
        } catch (IOException ioe) {
          return -1;
        }
      };
}
