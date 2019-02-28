package com.chrnie.initializer.exception;

public class CyclicDependencyException extends RuntimeException {

  public CyclicDependencyException(String message) {
    super(message);
  }
}
