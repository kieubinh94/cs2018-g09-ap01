package cs2018.ap.streaming.pipeline;

import java.io.IOException;

abstract class AbstractPipeline<T> {
  public abstract void build(String[] options) throws IOException;

  abstract T getResult();
}
