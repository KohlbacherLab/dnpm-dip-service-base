package de.dnpm.dip.service.query


import de.dnpm.dip.util.Logging


trait QueryCache[
  Criteria,
  Results,
  PatientRecord
]{
  self =>

  def timeoutSeconds: Int

  def newQueryId: Query.Id

  def add(
    queryWithResults: (Query[Criteria],Results)
  ): Unit

  final def += (
    queryWithResults: (Query[Criteria],Results)
  ) = add(queryWithResults)

  def queries: Seq[Query[Criteria]]

  def get(id: Query.Id): Option[(Query[Criteria],Results)]

  def getQuery(id: Query.Id): Option[Query[Criteria]] =
    self.get(id).map(_._1)

  def getResults(id: Query.Id): Option[Results] =
    self.get(id).map(_._2)
    
  def remove(id: Query.Id): Unit

  final def -= (id: Query.Id): Unit =
    self.remove(id)

}



class BaseQueryCache[
  Criteria,
  Results,
  PatientRecord
]
extends QueryCache[
  Criteria,
  Results,
  PatientRecord
]
with Logging
{

  import java.util.UUID.randomUUID
  import java.time.Instant.now
  import java.time.temporal.ChronoUnit.MINUTES
  import java.util.concurrent.Executors
  import java.util.concurrent.TimeUnit.SECONDS
  import scala.collection.concurrent._


  private val cache: Map[Query.Id,(Query[Criteria],Results)] =
    TrieMap.empty



  private val executor =
    Executors.newSingleThreadScheduledExecutor

  // Amount of inactivity time after which a query is removed
  private val timeOut =
    System.getProperty(
      "dnpm.dip.query.cache.timeout.minutes",
      15.toString  // 15 min default
    )
    .toInt 

  override val timeoutSeconds =
    timeOut * 60


  private val cleanUpPeriod =
    System.getProperty(
      "dnpm.dip.query.cache.cleanup.period.seconds",
      60.toString
    )
    .toLong


  executor.scheduleAtFixedRate(
    () => {

      log.debug("Running clean-up task for timed out Query sessions")

      cache.filterInPlace {
        case (_,(query,_)) => MINUTES.between(query.lastUpdate,now) < timeOut
      }
    },
    cleanUpPeriod, // delay  
    cleanUpPeriod, // period
    SECONDS
  )


  private val touch: Query[Criteria] => Query[Criteria] =
    _.copy(lastUpdate = now)


  override def newQueryId: Query.Id =
    Query.Id(randomUUID.toString)
  

  override def add(
    queryWithResults: (Query[Criteria],Results)
  ): Unit =
    cache += (queryWithResults._1.id -> queryWithResults)
  

  override def queries: Seq[Query[Criteria]] =
    cache.values
      .map(_._1)
      .toSeq


  override def get(id: Query.Id): Option[(Query[Criteria],Results)] = 
    cache.updateWith(id){
      case Some(query -> results) => Some(touch(query) -> results)
      case _          => None
    }


  override def remove(id: Query.Id): Unit =
    cache -= id

}
