package au.com.dius.pact.model

import au.com.dius.pact.model.generators.Category
import au.com.dius.pact.model.generators.Generators
import au.com.dius.pact.model.generators.RandomIntGenerator
import au.com.dius.pact.model.generators.RandomStringGenerator
import au.com.dius.pact.model.generators.UuidGenerator
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import spock.lang.Specification

class GeneratedRequestSpec extends Specification {
  private Generators generators
  private Request request

  def setup() {
    generators = new Generators()
    generators.addGenerator(Category.PATH, new RandomIntGenerator(400, 499))
    generators.addGenerator(Category.HEADER, 'A', UuidGenerator.INSTANCE)
    generators.addGenerator(Category.QUERY, 'A', UuidGenerator.INSTANCE)
    generators.addGenerator(Category.BODY, '$.a', new RandomStringGenerator())
    request = new Request(generators: generators)
  }

  def 'applies path generator for path to the copy of the request'() {
    given:
    request.path = '/path'

    when:
    def generated = request.generatedRequest()

    then:
    generated.path != request.path
  }

  def 'applies header generator for headers to the copy of the request'() {
    given:
    request.headers = [A: 'a', B: 'b']

    when:
    def generated = request.generatedRequest()

    then:
    generated.headers.A != 'a'
    generated.headers.B == 'b'
  }

  def 'applies query generator for query parameters to the copy of the request'() {
    given:
    request.query = [A: ['a', 'b'], B: ['b']]

    when:
    def generated = request.generatedRequest()

    then:
    generated.query.A != ['a', 'b']
    generated.query.A.size() == 2
    generated.query.B == ['b']
  }

  def 'applies body generators for body values to the copy of the request'() {
    given:
    def body = [a: 'A', b: 'B']
    request.body = OptionalBody.body(JsonOutput.toJson(body))

    when:
    def generated = request.generatedRequest()
    def generatedBody = new JsonSlurper().parseText(generated.body.value)

    then:
    generatedBody.a != 'A'
    generatedBody.b == 'B'
  }

}
