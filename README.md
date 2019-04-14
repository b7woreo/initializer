# Initializer [![Build Status](https://travis-ci.org/cheie/initializer.svg?branch=master)](https://travis-ci.org/cheie/initializer) [![Download](https://api.bintray.com/packages/chrnie/initializer/initializer/images/download.svg)](https://bintray.com/chrnie/initializer/initializer/_latestVersion)

A framework for initialize task of Android componentization.  

## Feature  
1. Auto discover tasks
2. Manage dependencies between tasks
3. Customize task running thread through executor

## Download
1. Add gradle plugin:
```
// Add in root project build.gradle
buildscript {
    dependencies {
        classpath "com.chrnie:initializer-gradle-plugin:$x.y.z"
    }
}

// Add in module project build.gradle
apply plugin: 'com.chrnie.initializer'
```

2. Add dependency:
```
implementation "com.chrnie:initializer:$x.y.z"
```

## Usage
[Sample Project](./sample)

## Inspiration
[知乎 Android 客户端组件化实践](https://zhuanlan.zhihu.com/p/45374964)

## License
```
MIT License

Copyright (c) 2019 ChenRenJie

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
