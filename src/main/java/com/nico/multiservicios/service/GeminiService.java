package com.nico.multiservicios.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GeminiService.class);

    public String getChatResponse(String userMessage) {
        try {
            logger.info("Preparando solicitud a Gemini API...");
            // Construir el cuerpo de la solicitud para Gemini 2.5 Flash
            // Estructura: { "contents": [{ "parts": [{ "text": "Mensaje" }] }] }
            // Construir el cuerpo de la solicitud
            ObjectNode rootNode = objectMapper.createObjectNode();

            // Agregar System Instruction (Contexto del sistema)
            ObjectNode systemInstruction = rootNode.putObject("system_instruction");
            ArrayNode systemParts = systemInstruction.putArray("parts");
            ObjectNode systemPart = systemParts.addObject();
            systemPart.put("text", "Eres el asistente virtual inteligente de 'Multiservicios NICO'. \n" +
                    "\n" +
                    "🚨 REGLAS CRÍTICAS (NO LAS ROMPAS):\n" +
                    "1. **NO TIENES ACCESO A LA BASE DE DATOS EN TIEMPO REAL**. No sabes qué productos hay, ni cuánto stock queda, ni quién compró qué.\n"
                    +
                    "2. **NUNCA INVENTES DATOS**. Si te preguntan '¿Cuánto cuesta el producto X?' o '¿Hay stock de Y?', responde: 'No tengo acceso a esa información en tiempo real, pero puedes verificarlo en la sección de Inventario'.\n"
                    +
                    "3. **TU ROL ES GUIAR Y EXPLICAR**. Ayuda al usuario a navegar el sistema y entender los procesos.\n"
                    +
                    "\n" +
                    "CONOCIMIENTO DEL SISTEMA (Úsalo para explicar, no para dar datos):\n" +
                    "- **Productos**: Tienen nombre, categoría, precio de compra/venta, stock (mínimo/máximo), marca, estado y ubicación.\n"
                    +
                    "- **Ventas**: Se registran con fecha, total, cliente, método de pago (EFECTIVO, TARJETA, YAPE, PLIN) y tipo de comprobante (BOLETA, FACTURA).\n"
                    +
                    "- **Clientes**: Tienen nombre, RUC/DNI, email, teléfono y dirección.\n" +
                    "- **Proveedores**: Se gestionan para reponer stock.\n" +
                    "\n" +
                    "EJEMPLOS DE RESPUESTA:\n" +
                    "- Usuario: '¿Cuánto vendí hoy?' -> Tú: 'No puedo ver tus ventas en vivo. Por favor, ve al módulo de **Reportes** o **Ventas** para ver el resumen diario.'\n"
                    +
                    "- Usuario: '¿Cómo registro un producto?' -> Tú: 'Ve a la sección **Inventario**, haz clic en **Nuevo Producto** y completa los campos obligatorios como nombre, precio y stock.'\n"
                    +
                    "\n" +
                    "Sé siempre amable, profesional y conciso.");

            // Agregar el mensaje del usuario
            ArrayNode contentsNode = rootNode.putArray("contents");
            ObjectNode contentNode = contentsNode.addObject();
            ArrayNode partsNode = contentNode.putArray("parts");
            ObjectNode partNode = partsNode.addObject();
            partNode.put("text", userMessage);

            String requestBody = objectMapper.writeValueAsString(rootNode);
            logger.debug("Request Body: {}", requestBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            // La URL debe incluir la API Key como query param
            String fullUrl = apiUrl + "?key=" + apiKey;
            logger.info("Enviando solicitud a URL: {}", apiUrl); // No loguear la key completa por seguridad

            ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, entity, String.class);
            logger.info("Código de respuesta Gemini: {}", response.getStatusCode());

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                // Navegar la respuesta: candidates[0].content.parts[0].text
                JsonNode candidates = responseJson.path("candidates");
                if (candidates.isArray() && candidates.size() > 0) {
                    JsonNode content = candidates.get(0).path("content");
                    JsonNode parts = content.path("parts");
                    if (parts.isArray() && parts.size() > 0) {
                        return parts.get(0).path("text").asText();
                    }
                }
            }
            logger.warn("Respuesta de Gemini no exitosa o formato inesperado: {}", response.getBody());
            return "Lo siento, no pude procesar tu solicitud en este momento.";

        } catch (Exception e) {
            logger.error("Error al comunicarse con Gemini API", e);
            return "Error al comunicarse con el asistente: " + e.getMessage();
        }
    }
}
