picture-voting
=====

## Building and Serving
picture-voting relies on sbt to build a jar and create a shell script to run it. It is recommended
that a third party sbt runner be used. [Paulp's sbt runner](https://github.com/paulp/sbt-extras)
is outstanding.

Once a suitable sbt script is on your path, you can build the binary with the following
```shell
$ sbt clean update compile stage
```

Once built, you can run the server from the root project directory
```shell
$ target/universal/stage/bin/picture-voting
```

### Dropbox configuration
To save time for this quick test, dropbox configuration is passed in at build time
```shell
$ sbt clean update compile stage -Ddropbox.accessToken=ACCESS_TOKEN -Ddropbox.folder=/burner
```
To handle folder management, at startup the application checks for the existence of a folder at
whatever has been specified. If the folder does not exist, it's created. If for some reason the folder
cannot be created, the application prints an error and fatally exits.

### Port
The port defaults to `8081`, but this can be set via the command line during build as well
```shell
$ sbt clean update compile stage -Ddropbox.accessToken=ACCESS_TOKEN -Ddropbox.folder=/burner -Dport=8080
```

## Endpoints
This application serves 3 endpoints instead of 2. I decided to make the `Report` endpoint more "RESTFul"
in it's design. Therefore, the `Report` endpoint now looks liks this
```shell
GET /reports => An array of reports for all images with votes
GET /report/PICTURE => a report detailing how many votes the image has, or a 404 if there are no votes found
```
The `POST /event` endpoint works as request.

### Admin interface
Using Twitter server gives you an admin Http endpoint for free. To view it, just point your browser at `/admin`, or hit the endpoint
via curl `curl -v -X GET http://wherever/admin/metrics.json`
