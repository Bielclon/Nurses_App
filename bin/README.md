# 🏥 Hospital G.O.A.T.E.D GOAT 🐐

## 🌟 Descripción del Proyecto

**`Nurses_App`** es una aplicación Java desarrollada con **Spring Boot** diseñada para optimizar y **digitalizar la gestión de tareas** del personal de enfermería. El propósito principal es **mejorar la eficiencia** del flujo de trabajo, reducir los errores de coordinación y permitir un **acceso rápido** a:

* **Registros de Pacientes:** Historia clínica, medicación y signos vitales.
* **Asignaciones y Turnos:** Distribución de pacientes y responsabilidades del personal.
* **Horarios y Calendario:** Gestión de turnos y disponibilidad.

Este sistema está enfocado en liberar tiempo administrativo para que los profesionales puedan centrarse en la atención al paciente.

---

## 🛠️ Instalación y Configuración

Sigue estos pasos para obtener una copia funcional del proyecto en tu máquina local.

### Prerrequisitos

Asegúrate de tener instalado lo siguiente:

* **Java Development Kit (JDK) 11 o superior**
* **Git**

### Pasos de Instalación

1.  **Clonar el Repositorio:**
    Abre tu terminal y usa el siguiente comando para clonar el proyecto:

    ```bash
    git clone [https://github.com/Bielclon/Nurses_App.git](https://github.com/Bielclon/Nurses_App.git)
    cd Nurses_App
    ```

2.  **Configuración de Dependencias:**
    El proyecto utiliza **Maven** para la gestión de dependencias. Los archivos necesarios se instalarán automáticamente la primera vez que ejecutes o compiles el proyecto.

3.  **Configuración de la Base de Datos (Opcional):**
    Si la aplicación requiere una base de datos externa (ej. PostgreSQL, MySQL), asegúrate de:
    * Tener el servicio de base de datos corriendo.
    * Configurar las credenciales en el archivo `src/main/resources/application.properties` (o `application.yml`).

    ```properties
    # Ejemplo de configuración de base de datos en application.properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/nurses_db
    spring.datasource.username=user
    spring.datasource.password=password
    ```

---

## 🚀 Uso de la Aplicación

### Ejecutar la Aplicación

Para iniciar la aplicación Spring Boot, usa el **Maven Wrapper** incluido:

```bash
./mvnw spring-boot:run
