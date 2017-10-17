package company


object CompanyCanonicaliser {

  def canonicaliseName(input: String) = {

    val lowercased = input.toLowerCase

    val withoutLtd =
      lowercased.replaceAll("limited", "").
      replaceAll("ltd", "").
      replaceAll("incorporated", "").
      replaceAll("inc", "")

    val withoutPunctuation = withoutLtd.replaceAll("[^a-z0-9]", "")

    val withoutNone =
      if (withoutPunctuation == "na" ||
        withoutPunctuation == "none" ||
        withoutPunctuation == "no" ||
        withoutPunctuation == "me" ||
        withoutPunctuation == "notgiven" ||
        withoutPunctuation == "notapplicable") "" else
      withoutPunctuation
    withoutNone
  }

  def canonicalisePhone(input: String) = {
    input.replaceAll("\\D", "")
  }
}
