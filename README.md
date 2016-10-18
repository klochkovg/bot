# Bot
Simple application according to requirements of potential employer.

## Prerequisites
* Java Development Kit (at least 7, tested only for 8)
* Apache Maven
* RabbitMQ message broker

### Rabbit MQ 
Its distribution can be downloaded from the official web site [www.rabbitmq.com](http://www.rabbitmq.com/download.html)

For installation on Debian derived Linux systems use 

`apt-get install rabbitmq-server`

## Build
Building of application is usual for all maven projects. In the top directory of distribution execute the command:

`mvn clean package`

The resulting package could be found in target directory in two versions. With and without dependencies.
 
* `bot-0.1-SNAPSHOT-jar-with-dependencies.jar`
* `bot-0.1-SNAPSHOT.jar`

## Main commands
To run the application use command 

`java -jar <pathToJarFile> <command name> <arguments>`

At the time of program execution RabbitMQ broker should be launched on the same system with default credentials.

Available commands:

* *schedule* - Add filenames to resize queue
* *resize* - Resize next images from the queue
* *status* - Output current status in format <queue> <number_of_images>
* *upload* - Upload next images to remote storage

## Config file
Configuration file is situated in directory of launch with name config.json. It should contain name of temporary directory, name of Amazon S3 bucket and its credentials. List of parameters is below: 

* aws_access_key - credentials of S3
* aws_secret_access_key - credentials of S3
* bucketName - name of S3 bucket
* tmpDirectoryName - directory for resized pictures storing
* queue_host - name of RabbitMQ host (should be localhost)