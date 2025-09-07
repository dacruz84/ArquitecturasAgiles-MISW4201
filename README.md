# Participación de los Integrantes

Los proyectos de este repositorio contienen la unificación de los repositorios de cada integrante del grupo :

https://github.com/NicolasInfanteUniandes96/ArquitecturasAgiles-Logistica-01.git

https://github.com/danrulloa/arquitecturasAgiles-Logistica-L02.git

https://github.com/cvillamizarL/route-picking-3opt-mid.git

https://github.com/dacruz84/ArquitecturasAgiles-Voting.git


# Como correr este proyecto?

Ejecute el sieguiente comando para poder hacer el build de las imágenes

```
docker compose -f compose.yaml build
```

Ejecute el siguiente comando para levantar todo el ecosistema
```
docker compose -f compose.yaml up -d
```

Al finalizar este comando debería ver en su docker desktop todos los contenedores arriba:

![Contenedores](Resources/DockerContainers.png)


# Como ejecutar la prueba con K6?

Por favor instale K6, de acuerdo a su sistema operativo, puede seguir las instrucciones en el siguiente link: https://grafana.com/docs/k6/latest/set-up/install-k6/#docker.

Una vez K6 esta instalado, dirijase a la carpeta `K6`, ahí encuentra un archivo con el nombre de `test.js`, por favor abra una terminal y ejecute el siguiente comando:

```
k6 run .\test.js
```

Este comando ejecutará una prueba sobre el ecosistema previamente levantado.

Particularmente se empezarán a enviar peticiones al componente voting y se podrá evidenciar que siempre responde correctamente, incluso cuando el servicio de Java tiene fallas el en 10% de las peticiones.

![K6 Test](Resources/K6.png)

```
Nota:

Recuerde que el archivo de la prueba puede ser modificado para ejecutar diferentes escenarios.
```

# Cómo verificar el comportamiento de los microservicios durante la prueba?

Como parte del escositema, tenemos un contenedor de Grafana que ha sido configurado para recibir tanto los logs como las métricas.

Para usar el dashboard configurado, puede dirigirse a: http://localhost:3000/d/ddc8d4f2-11cd-49d7-a645-05432d7601b9/technical?orgId=1&from=now-5m&to=now&refresh=5s

Eso lo debe llevar a una página donde podrá ver el comportamiento de cada microservicio:

![Grafana](Resources/Grafana.png)

