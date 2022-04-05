package au.com.sixtree.blog

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.rest.RestBindingMode
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Component

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Component
class RestRoute extends RouteBuilder {
 
  @Value('${rest.host}') String host
  @Value('${rest.port}') String port

  @Override
  void configure() throws Exception {
    restConfiguration()
      .component('jetty')
      .host(host).port(port)
      .bindingMode(RestBindingMode.json)

    rest('/things')
      .post()
        .type(Thing)
        .to('direct:createThing')
      .get()
        .outType(ThingSearchResults)
        .to('direct:getThings')
      .get('/{id}')
        .outType(Thing)
        .to('direct:getThing')
      .delete('/{id}')
        .outType(Thing)
        .to('direct:removeThing')

    from('direct:createThing')
      .to('jpa:au.com.sixtree.blog.Thing')

    from('direct:getThing')
      .to('sql:select * from THING where id = :#${header.id}?outputType=SelectOne')
      .bean('transformer', 'mapThing')

    from('direct:getThings')
      .setProperty('query').method('transformer', 'constructQuery(${headers})')
         .process(new Processor() {
         @Override
         void process(Exchange exchange) throws Exception {
           println "Hello world"
         }
       })
      .toD('sql:$simple{exchangeProperty.query}')
      .bean('transformer', 'mapThingSearchResults')

    from('direct:removeThing')
      .to('direct:getThing')
      .setProperty('thing', body())
      .to('sql:delete from THING where id = :#${body.id}')
  }
}

@Entity(name = "THING")
class Thing {
  @Id @GeneratedValue @Column(name = "ID") Integer id
  @Column(name = "NAME") String name
  @Column(name = "OWNER") String owner
}

class ThingSearchResults {
  Integer size
  List<Thing> things
}

@Component('transformer')
class Transformer {
  Thing mapThing(Map map) {
    new Thing(id: map.id, name: map.name, owner: map.owner)
  }

  String constructQuery(Map headers) {
    println "MHB headers = $headers"
    def wheres = []
    if (headers.name) {
      wheres << 'name = :#${header.name}'
    }
    if (headers.owner) {
      wheres << 'owner = :#${header.owner}' 
    }

    def query = 'select * from THING'
    if (wheres) {
      query += ' where ' + wheres.join(' and ')
    }
    return query
  }

  ThingSearchResults mapThingSearchResults(List<Map> body) {
    new ThingSearchResults(
      size: body.size(),
      things: body.collect { mapThing it }
    )
  }
}

@SpringBootApplication
class Application {
  public static void main(String[] args) {
    SpringApplication.run Application, args
  }
}
