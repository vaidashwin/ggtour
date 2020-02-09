# GGTour

GGTour is a website/Discord bot to provide a ladder
system in the vein of PGTour or WGTour. Players will
find matches in the Discord and then upload replays
(to the site or via a bot tool) in order to update
their Elo and standing.

See [Use Cases](USE_CASES.md) for more design details.

## Getting Started (WIP)

#### Prerequisites:
1) SBT 1.2.1+ (https://www.scala-sbt.org/download.html)
1) Postgres 12+ (https://www.postgresql.org/download/)
1) Redis 5.0+ (https://redis.io/download)

#### Run steps
1) Clone the repository.
1) Copy `core/src/main/resources/application-user.conf.template` and
add your Redis/Postgress/Discord values. SBT requires these for
migration steps, so you need something here even if it's junk.
1) Run `sbt` within the repository root. It should start in the
 `ggtour` project.
1) Run `flywayMigrate` to create your database if the Postgres values
above are real (if you're doing DB independent dev, this can be 
skipped).
1) Run `reStart` to start up the sandbox environment.

## Architecture/Design
GGTour is intended to have two points of entry: a
website (planned to be https://ggtour.io) and a Discord
bot which will inhabit servers for each of the formats.
Behind these points of entry, various services in the
cluster will handle the concrete tasks of responding to
requests.

#### Cluster Organization
Each cluster will have multiple nodes, divided into roles
which define what services are hosted on those nodes. Each
node is run on a specific machine. The overall design
intention with a service on a cluster node is that it 
should be interchangeable at runtime with any other one;
that is if you run the same request twice, any two nodes
with appropriate role can handle it equivalently. If a request/
response pattern is used, it is intended that the same actor
will handle the response (since it will need to load in its
own behavior). Therefore requests will include a replyTo value.

Each node in the cluster will run the service JVM which will host
an `ActorSystem` for the service. Each `ActorSystem` will interact
with others by the `RemotingFacade`, which will create actors that
abstract out the location of the target service; this is for clustered
deployments.

In a clustered deployment, Kafka will be used for interservice messaging
and the `RemotingFacade` will spawn a mock actor whose job is to read/write
from the Kafka stream without visibility to the service. 

The cluster will also include Redis hosts for short term storage across
services (primarily used for ladder) and a Postgres db for backend
storage. 

#### Project Organization
There are two types of sbt projects defined in this repo.
The first one is service projects, which represents the
code that would be deployed to any node in the cluster 
with a matching role, and any necessary configuration
for that node. The second is "library" projects which 
provide various shared functionality and are not intended
to be startable. They will be included as dependencies
in other service project. Each leaf node service should
not be depended upon; if you need cross-service access
to classes/types/other resources, a new library project
should be added (or an existing one modified) in order
to hold the shared dependencies.