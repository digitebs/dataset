### prerequisites
maven

jdk 8


### build
```
mvn clean compile test package
```

Running the test will create folder called test in the current workspace.

### run

```shell script
./run.sh test 5 "x,10"

./run.sh test "x,10"
```

### Notes

Since I wanted exactly split the file equals to the given capped size; I consider newline as 1 bytes and
 empty strings must be allowed.