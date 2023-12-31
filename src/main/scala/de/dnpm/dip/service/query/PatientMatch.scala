package de.dnpm.dip.service.query


import de.dnpm.dip.model.{
  Id,
  Site,
  Age,
  Patient,
  Gender,
  VitalStatus
}
import de.dnpm.dip.coding.Coding
import play.api.libs.json.{
  Json,
  Format,
  OFormat
}


final case class PatientMatch[Criteria]
(
  id: Id[Patient],
//  managingSite: Coding[Site],
  managingSite: Option[Coding[Site]],
  gender: Coding[Gender.Value],
  age: Age,
  vitalStatus: Coding[VitalStatus.Value],
  matchingCriteria: Criteria
)



object PatientMatch
{

  def of[Criteria](
    patient: Patient,
    matchingCriteria: Criteria
  ): PatientMatch[Criteria] =
    PatientMatch(
      patient.id,
//      patient.managingSite.get, // Option.get safe here: site is set upon entry of a dataset into the DNPM system 'context' (i.e. upon import)
      patient.managingSite, // Always defined: site is set upon entry of a dataset into the DNPM system 'context' (i.e. upon import)
      patient.gender,
      patient.age,
      patient.vitalStatus,
      matchingCriteria
    )


  implicit def format[Criteria: Format]: OFormat[PatientMatch[Criteria]] =
    Json.format[PatientMatch[Criteria]]
}

