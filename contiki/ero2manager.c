/**
* \file
* 		ErO2 Manager which implements the service composition logic to certain extent
* \author
* 		Kasun Samarasinghe <Kasun.Wijesiriwardana@unige.ch>
*/
#include "contiki-conf.h"
#include "contiki-net.h"
#include <stdio.h>

#include "erbium.h"
#include "er-coap-07-engine.h"
#include "ero2client.h"
#include "ero2registry.h"

static struct etimer et;
static int PROCESS_INTERVAL=DEFAULT_INTERVAL;
process_event_t STATE_CHANGED;

RESOURCE(ero2heartbeat, METHOD_GET, "ero2heartbeat", "title=\"Heartbeat\"");
void ero2heartbeat_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
  char* message = "Heartbeat";
	memcpy(buffer, message, 10);

  REST.set_header_content_type(response, REST.type.TEXT_PLAIN); 
  REST.set_header_etag(response, 10, 1);
  REST.set_response_payload(response, buffer, 10);
}

RESOURCE(ero2register, METHOD_GET, "ero2register", "title=\"Register\"");
void ero2register_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{

  REST.set_header_content_type(response, REST.type.TEXT_PLAIN); 
  REST.set_header_etag(response, 10, 1);
  REST.set_response_payload(response, buffer, 10);
}


PROCESS(ero2_process,"ErO2 Process");
PROCESS_THREAD(ero2_process, ev, data)
{
	PROCESS_BEGIN();
	printf("ErO2 Process starts!\n");

	/* Define the timer to fire periodically and initially set it to the INIT interval */
	etimer_set(&et, CLOCK_SECOND * PROCESS_INTERVAL);

	while(1){

		PROCESS_YIELD();

		struct ero2_service remote_service;
		char payload[MAX_PAYLOAD_LENGTH];		

		switch(ero2_state){
			case INIT:
				/* Register services in the centralized profiling registry */
				printf("Registering services ..\n");
				
				/* Prepare service */
				PROFILING_SERVER(&remote_service.ipaddr);
				remote_service.uri="register";
				remote_service.request_method=COAP_POST;

				/*Prepare payload */			
				sprintf(payload,"%s",service_pointer);
				get_service_serialization(payload);
				PRINTF("Payload %s\n",payload);
				
				invoke_service(&remote_service,payload);
				break;
			case REGISTERED:
				/* Register the node with its parents */
				/* Prepare service */
				remote_service.ipaddr=parent.ipaddr;
				remote_service.uri="register";
				remote_service.request_method=COAP_POST;

				/*Prepare payload */
				uip_ds6_addr_t *addr;
				addr = uip_ds6_get_global(ADDR_PREFERRED);
				if(addr != NULL) {
  				PRINT6ADDR(&addr->ipaddr);
				}		
				char hostaddrstring[50];
				serialize_address(&addr->ipaddr,hostaddrstring); 	
				sprintf(payload,"%s|%s",service_pointer,hostaddrstring);
				PRINTF("Payload %s\n",payload);
				
				invoke_service(&remote_service,payload);
				break;
			case DISCOVERED:
				/* Periodic hearbeat report to the server */
				/* Prepare service */
				PROFILING_SERVER(&remote_service.ipaddr);
				remote_service.uri="heartbeat";
				remote_service.request_method=COAP_POST;

				/*Prepare payload */
				sprintf(payload,"%s|%s",service_pointer,hostaddrstring);
				PRINTF("Payload %s\n",payload);
				
				invoke_service(&remote_service,payload);
				break;
		}	
		
		/* Sets etimer to periodically fire if normal scheduling is failed */
		if(etimer_expired(&et)){
			etimer_set(&et, CLOCK_SECOND * PROCESS_INTERVAL);
		}
	}	

  PROCESS_END();
}

/**
*	Starts ero2 service coordination layer
*/
void start_ero2_service(char* sp)
{
	//Initialize rest engine
	rest_init_engine();

	//Activates ero2 services
	service_pointer=sp;
	init_registry();
	rest_activate_resource(&resource_ero2heartbeat);
	rest_activate_resource(&resource_ero2register);

	//Activates application services
	start_app_services();
	//Starts ero2 process
	process_start(&ero2_process,NULL);
}


/**
* Handles responses from ero2 services
*/

void service_handler(void* response)
{
	PRINTF("Response received\n");

	/* Read the response */
	uint8_t *response_payload;
  int len = coap_get_payload(response, &response_payload);

  PRINTF("Payload received %s\n",  (char *)response_payload);	
	
	int status;
	if(response_payload != NULL){
		switch(ero2_state){
			case INIT:	
				status=parse_response(response_payload);				
				if(status){
					PRINTF("Registration successful\n");
								
					/* Sets the parent according to the central server */
					set_parent(response_payload);
					ero2_state=REGISTERED;
					printf("process changed status\n");
				}else{
					PRINTF("Error registration..\n");
				}		
				break;

			case REGISTERED:
				status=parse_response(response_payload);				
				if(status){
					PRINTF("Discovered successful\n");
					ero2_state=DISCOVERED;
				}else{
					PRINTF("Error in discovery..\n");
				}	
				break;

			case DISCOVERED:
				printf("asd"); 	
				break;
		}
	}else{
		PRINTF("NULL response\n");
	}
}

int parse_response(char* response_payload)
{
	return 1;
}


void set_parent(char* response_payload)
{

}

void  serialize_address(uip_ipaddr_t* addr,char* addrstring) 
{
	sprintf(addrstring,"[%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x]", ((uint8_t *)addr)[0], ((uint8_t *)addr)[1], 	((uint8_t *)addr)[2], ((uint8_t *)addr)[3], ((uint8_t *)addr)[4], ((uint8_t *)addr)[5], ((uint8_t *)addr)[6], ((uint8_t *)addr)[7], ((uint8_t 	*)addr)[8], ((uint8_t *)addr)[9], ((uint8_t *)addr)[10], ((uint8_t *)addr)[11], ((uint8_t *)addr)[12], ((uint8_t *)addr)[13], ((uint8_t *)addr)[14], ((uint8_t *)addr)[15]);
}


