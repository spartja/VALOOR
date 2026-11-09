VALOOR: Es una aplicacion web diseñada para facilitar la administracion y comercializacion de articulos deportivos, como implementos, calzados, ropas y accesorios relacionados con diferentes disciplinas deportivas, entre ellas el futbol, tenis, natacion, boxeo, atletismo y senderismo.
el proyecto surge de la necesidad de mejorar los procesos de pequeños y medianos establecimientos comerciales que actualmente gestionan sus productos, inventarios y pedidos mediante cuadernos, hojas de calculo y otros medios manuales. estas practicas pueden generar problemas como informacion desactualizada, dificultades para controlar las existencias y poca orgganizacion para el seguimiento de los pedidos.
por esta razon VALOOR propone una solucion tecnologica que permita centralizar la informacion del negocio mediante una plataforma web, facilitando el control de los productos, la administracion del inventario y la gestion de compras realizadas por los clientes.
 Funcionalidades principales:
 Gestión de usuarios: registro, inicio de sesión y administración de roles.
Gestión de productos: registrar, consultar, actualizar y eliminar artículos deportivos.
Organización por deportes: clasificar los productos según la disciplina deportiva, categoría y marca.
Control de inventario: administrar las existencias de los productos, incluyendo sus tallas y cantidades disponibles.
Catálogo de productos: permitir la búsqueda y consulta de artículos según diferentes criterios.
Carrito de compras: facilitar la selección de productos por parte de los clientes.
Gestión de pedidos: registrar pedidos y controlar sus estados: pendiente, confirmado, enviado, entregado o cancelado.
Reportes básicos: consultar información relacionada con las ventas y los movimientos comerciales.
Estas funcionalidades corresponden al alcance propuesto para el prototipo y se implementarán progresivamente.

 Tecnologías utilizadas : Java 17, Spring Boot, MySQL, Visual Studio Code, Postman, Git y GitHub.
 Configuración de la base de datos

Crear una base de datos en MySQL con el siguiente nombre: valoor_db.
Después, configurar las credenciales de conexión en el archivo application.properties, ubicado en:
src/main/resources/application.properties.
Una vez configurada la base de datos, seguir estos pasos:
Paso 1. Abrir la carpeta del proyecto VALOOR en Visual Studio Code.
Paso 2. Verificar que Java 17 esté instalado: java - version
Ejecutar la aplicación: .\mvnw.cmd spring-boot:run
Cuando la aplicación inicie correctamente, estará disponible en: http://localhost:8080

Para ejecutar las pruebas automatizadas: .\mvnw.cmd test
También se podrá utilizar Postman para verificar el funcionamiento de los servicios REST que se vayan implementando.

VALOOR está orientado a la gestión de productos, inventarios y pedidos de una tienda de artículos deportivos. En esta primera versión no se contempla la integración con pasarelas de pago reales, aplicaciones móviles nativas ni servicios de logística y transporte.

El proyecto se encuentra en desarrollo académico y sus funcionalidades se incorporarán de acuerdo con las etapas de implementación.


