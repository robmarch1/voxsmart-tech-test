# VoxSmart Tech Test

This repo contains my solution to the VoxSmart tech test, including both the requested `NumberParser` class and unit tests to validate that it works as expected.

I have assumed the following:
- A "country" refers to a geographical region with a single dialling code rather than a geographical region under a single government
- All country dialling codes are unique - if the US is +1 then no other country is. 
  *Note: this is not the case in the real world, where many North American countries are treated as regions within the +1 dialling code*
- The examples provided in the assignment are correct (if not complete) as these form the core of the happy path tests.
- All valid country codes are included in the country dialling code map at runtime.
- All international calls are prefixed by the '+' symbol.
  *Note: This is not necessarily the case in the real world as historically this character did not exist on older landline phones.*
- The user is always located in the same country that their phone number is registered in. 
  *E.g. a +44 number calling with the trunk prefix 0 is always a UK phone number, located in the UK, calling another UK phone number.*
- It is never legitimate to dial a local number without including the trunk prefix. 
  *Note: This is not the case in the real world e.g. calling a landline number from a landline in the same city.*

The following are improvements that would be required for me to consider this to be clean code but were expressly forbidden by the test:
- Improved error handling. This could be done by either using checked exceptions or returning an object with an error field instead of a simple String.
  The latter approach is more performant, while the former provides a clean approach to returning a semantically meaningful HTTP status code in the context of a Spring Rest API.
  At present, the `RuntimeException`s are less than ideal as the calling code does not explicitly know what could go wrong and may therefore fail to properly handle the error modes.
- Improved use of the type system. By using an enum for country codes (i.e. "GB", "FR", "US"), we prevent easily avoidable human errors e.g. referring to the UK as "UK" instead of "GB" as well as inconsistencies between the two maps.
  Additionally, trunk prefixes should not be stored in Strings as these are purely numeric.
- The completeness of the country code and trunk prefix maps should be validated prior to runtime.
  Currently there is a failure mode for the case that a country key in the country code map is missing from the trunk prefix map.
  This should be validated by integration tests instead. Additionally, this config should not be held in mutable state.
