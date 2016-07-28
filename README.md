# Cassandra Data Loader

Engine to load data into Cassandra pre-existing tables.

## Setup
```
vagrant up
```
### Requirements
* [VirtualBox](https://www.virtualbox.org/wiki/Downloads)
* [Vagrant](https://www.vagrantup.com/downloads.html)

## Tests Execution (Apache Maven)
```
vagrant ssh
mvn clean test
```

## How to
```
import com.github.vezzoni.cassandra.data.loader.dataloader.DataLoader;
import com.github.vezzoni.cassandra.data.loader.dataloader.LoadActionEnum;
import com.github.vezzoni.cassandra.data.loader.dataset.DataSet;
import com.github.vezzoni.cassandra.data.loader.dataset.xml.XmlDataSet;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import java.io.InputStream;
import java.util.List;
...

Cluster cluster = Cluster.builder()
    .addContactPoints("localhost")
    .withPort(9042)
    .withCredentials("cassandra", "cassandra")
    .build();

Session session = cluster.connect("yourKeySpace");

DataLoader dataLoader = new DataLoader(session);

InputStream is = this.getClass().getClassLoader().getResourceAsStream("dataset.xml");
DataSet dataSet = new XmlDataSet(is);

dataLoader.load(dataSet, LoadActionEnum.TRUNCATE_AND_CREATE);

ResultSet result = session.execute("select * from employee");

List<Row> rows = result.all();

Row row = rows.get(0);

int id = row.getInt("id");
```

## Dataset format
The Cassandra Data Loader will load table metadata in order to convert the columns values to their respective types.

The table name must be the tag name and, the table columns must to be configured as attributes of that tag.

```
<?xml version='1.0' encoding='UTF-8'?>
<dataset>
    <employee id="1" first_name="Chuck" last_name="Norris" active="true"/>
    <employee id="2" first_name="Bruce" last_name="Lee" active="true"/>
    <employee id="3" first_name="Capitão" last_name="Nascimento" active="true"/>
</dataset>
```

