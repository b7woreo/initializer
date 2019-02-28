package com.chrnie.initializer.exception;

public class DependencyNotFoundException extends RuntimeException {

  public DependencyNotFoundException(String message) {
    super(message);
  }
}
