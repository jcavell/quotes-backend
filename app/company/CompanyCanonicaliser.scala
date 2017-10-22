package company


object CompanyCanonicaliser {

  def canonicaliseName(input: String) = {

    val lowercased = input.toLowerCase
    val withoutPunctuation = lowercased.replaceAll("[^a-z0-9]", "")

    val withoutLtd =
      withoutPunctuation.replaceAll("limited", "").
      replaceAll("ltd$", "").
      replaceAll("incorporated$", "").
      replaceAll("inc$", "")

    val withoutNone =
      if (withoutLtd == "na" ||
        withoutLtd == "none" ||
        withoutLtd == "no" ||
        withoutLtd == "me" ||
        withoutLtd == "notgiven" ||
        withoutLtd == "notapplicable") "" else
        withoutLtd
    withoutNone
  }

  def canonicalisePhone(input: String) = {
    input.replaceAll("\\D", "")
  }
}
