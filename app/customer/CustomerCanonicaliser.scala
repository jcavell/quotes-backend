package customer

object CustomerCanonicaliser {

  def canonicaliseName(input: String) = {
    val withoutTitle = input.toLowerCase.replaceAll("^mrs\\.? ", "").replaceAll("^mr\\.? ", "").replaceAll("^ms\\.? ", "").replaceAll("^miss\\.? ", "").replaceAll("^dr\\.? ", "")
    val withoutPunctuation = withoutTitle.replaceAll("[^a-z0-9]", "")
    withoutPunctuation
  }

  def canonicaliseEmail(input: String) = input.toLowerCase.replaceAll("\\s", "")

  def canonicaliseMobile(input: String) = input.replaceAll("\\D", "")
}
