package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("⚡Elvan Instalaciones")
                        .version("1.0.0")
                        .description(generarDescripcion())
                        .contact(new Contact()
                                .name("Soporte Técnico Elvan")
                                .email("shermidagre@gmail.com"))
                              //  .url("https://www.elvaninstalaciones.com"))
                        .license(new License()
                                .name("Propiedad Privada - Uso Interno")));
    }

    private String generarDescripcion() {
        return "### Centro de Control Operativo\n" +
               "Bienvenido al panel de gestión digital de **Elvan Instalaciones**. Esta interfaz permite interactuar con el corazón de la logística y el personal de la empresa de forma centralizada.\n\n" +
               
               "#### 📋 Módulos del Sistema\n" +
               "* **Gestión de Personal:** Control de acceso, perfiles de técnicos y monitoreo de actividad en tiempo real mediante el sistema de 'latidos'.\n" +
               "* **Control de Inventario:** Catálogo maestro de artículos y materiales eléctricos.\n" +
               "* **Administración de Obras:** Registro y seguimiento de los proyectos activos donde se ejecutan las instalaciones.\n" +
               "* **Trazabilidad de Movimientos:** Registro histórico de entradas y salidas de material, garantizando que cada artículo esté localizado.\n" +
               "* **Asistente Inteligente:** Interfaz con tecnología Gemini para apoyo en consultas operativas.\n\n" +
               
               "#### 📖 Guía\n" +
               "Si es su primera vez utilizando esta herramienta, siga estos pasos:\n" +
               "1. **Seleccione un módulo:** Haga clic en cualquiera de las secciones (Usuarios, Artículos, etc.) para ver las acciones disponibles.\n" +
               "2. **Explorar acción:** Al abrir una acción (ej. 'Obtener todos los artículos'), verá el botón **'Try it out'** (Probar).\n" +
               "3. **Ejecutar:** Tras pulsar el botón, haga clic en el recuadro azul **'Execute'** para obtener la información actualizada del sistema.\n" +
               "4. **Revisar:** El sistema le devolverá una respuesta con los datos solicitados en la sección inferior.";
    }
}