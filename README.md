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
2) Postgres 12+ (https://www.postgresql.org/download/)
3) Redis 5.0+ (https://redis.io/download)

#### Run steps
1) Clone the repository.
2) Run `sbt` within the repository root. It should start in the `ggtour` project.
3) Run `reStart` to start up the sandbox environment.

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
with appropriate role can handle it equivalently.

Each node in the cluster will have up to two main
processes running:
1) Service JVM
2) Redis node

The Redis node will be used as a cluster cache and will
be clustered as well so as to synchronize each node. The
nodes will ultimately be scaled up and down in order to
handle load/be fault tolerant.

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