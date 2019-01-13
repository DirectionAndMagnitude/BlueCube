#include <project.h>

uint16 fingerPos    = 0xFFFF;
uint16 fingerPosOld = 0xFFFF;


uint8 nSamples              = 20;
uint8 analogIn[500]         ={0};
uint8 analogInOld[500]      ={0};
uint16 a;
int capsenseNotify;

/*******************************************************************************
* Macros & Declarations for MTU Exchange
*******************************************************************************/
#define MAX_MTU_SIZE                (512)
#define DEFAULT_MTU_SIZE            (23)

uint8 cccdNotifEnabledValue[] = {0x01, 0x00};

CYBLE_GATT_VALUE_T cccdNotifFlagSetStruct =
{
    cccdNotifEnabledValue,
    2,
    0
};






/***************************************************************
 * Function to update the LED state in the GATT database
 **************************************************************/
//uint16 analogIn    =  0xFFFF;
//uint16 analogInOld  = 0xFFFF;
//void updateAnalogIn()
//{
//    
//    if(CyBle_GetState() != CYBLE_STATE_CONNECTED)
//        return;
//    
//    CYBLE_GATTS_HANDLE_VALUE_NTF_T 	tempHandle;
//   
//    tempHandle.attrHandle = CYBLE_LEDCAPSENSE_ANALOGIN_CHAR_HANDLE;
//  	tempHandle.value.val = (uint8 *) &analogIn;
//    tempHandle.value.len = 2; 
//    CyBle_GattsWriteAttributeValue(&tempHandle,0,&cyBle_connHandle,CYBLE_GATT_DB_LOCALLY_INITIATED);  
//    
//    /* send notification to client if notifications are enabled and finger location has changed */
//    if (capsenseNotify  && (analogIn != analogInOld) )//TODO:  Always Updating
//    
//        CyBle_GattsNotification(cyBle_connHandle,&tempHandle);
//        analogInOld = analogIn;
//    
//}


void updateAnalogIn()
{
 
    if(CyBle_GetState() != CYBLE_STATE_CONNECTED)
        return;
    
    CYBLE_GATTS_HANDLE_VALUE_NTF_T 	tempHandle;
    
    //Here we update nSamples with new value from request
    nSamples = CYBLE_GATT_MTU;
   
    tempHandle.attrHandle = CYBLE_LEDCAPSENSE_ANALOGIN_CHAR_HANDLE;
  	tempHandle.value.val = (uint8 *) &analogIn;
    tempHandle.value.len = nSamples;  //TODO:  CHANGED TO 23
    CyBle_GattsWriteAttributeValue(&tempHandle,0,&cyBle_connHandle,CYBLE_GATT_DB_LOCALLY_INITIATED);  
    
    /* send notification to client if notifications are enabled and finger location has changed */
    if (capsenseNotify)//&& (analogIn[0] != analogInOld[0]) )//TODO:  Always Updating
        CyBle_GattsNotification(cyBle_connHandle,&tempHandle);

    
}


/***************************************************************
 * Function to update the LED state in the GATT database
 **************************************************************/
void updateLed()
{
    CYBLE_GATTS_HANDLE_VALUE_NTF_T 	tempHandle;
   
    uint8 red_State = !red_Read();
    
    if(CyBle_GetState() != CYBLE_STATE_CONNECTED)
        return;
    
    tempHandle.attrHandle = CYBLE_LEDCAPSENSE_LED_CHAR_HANDLE;
  	tempHandle.value.val = (uint8 *) &red_State;
    tempHandle.value.len = 1;
    CyBle_GattsWriteAttributeValue(&tempHandle,0,&cyBle_connHandle,CYBLE_GATT_DB_LOCALLY_INITIATED);  
}

/***************************************************************
 * Function to update the CapSesnse state in the GATT database
 **************************************************************/
void updateCapsense()
{
    if(CyBle_GetState() != CYBLE_STATE_CONNECTED)
        return;
    
	CYBLE_GATTS_HANDLE_VALUE_NTF_T 	tempHandle;
    
    tempHandle.attrHandle = CYBLE_LEDCAPSENSE_CAPSENSE_CHAR_HANDLE;
  	tempHandle.value.val = (uint8 *)&fingerPos;
    tempHandle.value.len = 2; 
    CyBle_GattsWriteAttributeValue(&tempHandle,0,&cyBle_connHandle,CYBLE_GATT_DB_LOCALLY_INITIATED );  
    
    /* send notification to client if notifications are enabled and finger location has changed */
    if (capsenseNotify && (fingerPos != fingerPosOld) )
        CyBle_GattsNotification(cyBle_connHandle,&tempHandle);
        fingerPosOld = fingerPos;
}

/***************************************************************
 * Function to handle the BLE stack
 **************************************************************/
void BleCallBack(uint32 event, void* eventParam)
{
    CYBLE_GATTS_WRITE_REQ_PARAM_T *wrReqParam;
    
    
    /* for MTU Exchange */
    CYBLE_GATTC_WRITE_REQ_T writeRequestData =
    {
        cccdNotifFlagSetStruct,
        0x000F
    };
   
    switch(event)
    {
        /* if there is a disconnect or the stack just turned on from a reset then start the advertising and turn on the LED blinking */
        case CYBLE_EVT_STACK_ON:
        case CYBLE_EVT_GAP_DEVICE_DISCONNECTED:
            capsenseNotify = 0;
            CyBle_GappStartAdvertisement(CYBLE_ADVERTISING_FAST);
            pwm_Start();
           
            
        break;
        
        /* when a connection is made, update the LED and Capsense states in the GATT database and stop blinking the LED */    
        case CYBLE_EVT_GATT_CONNECT_IND:
            updateLed();
            updateCapsense();  
            updateAnalogIn();
            pwm_Stop();
		break;

        /* handle a write request */
        case CYBLE_EVT_GATTS_WRITE_REQ:
            wrReqParam = (CYBLE_GATTS_WRITE_REQ_PARAM_T *) eventParam;
			      
            /* request write the LED value */
            if(wrReqParam->handleValPair.attrHandle == CYBLE_LEDCAPSENSE_LED_CHAR_HANDLE)
            {
                /* only update the value and write the response if the requested write is allowed */
                if(CYBLE_GATT_ERR_NONE == CyBle_GattsWriteAttributeValue(&wrReqParam->handleValPair, 0, &cyBle_connHandle, CYBLE_GATT_DB_PEER_INITIATED))
                {
                    red_Write(!wrReqParam->handleValPair.value.val[0]);
                    CyBle_GattsWriteRsp(cyBle_connHandle);
                }
            }
            
            /* request to update the CapSense notification */
            if(wrReqParam->handleValPair.attrHandle == CYBLE_LEDCAPSENSE_CAPSENSE_CAPSENSECCCD_DESC_HANDLE) 
            {
                CyBle_GattsWriteAttributeValue(&wrReqParam->handleValPair, 0, &cyBle_connHandle, CYBLE_GATT_DB_PEER_INITIATED);
                capsenseNotify = wrReqParam->handleValPair.value.val[0] & 0x01;
                CyBle_GattsWriteRsp(cyBle_connHandle);
            }		
            
            
            /* request to update the AnalogIn notification */
            if(wrReqParam->handleValPair.attrHandle == CYBLE_LEDCAPSENSE_ANALOGIN_AINCCCD_DESC_HANDLE) 
            {
                CyBle_GattsWriteAttributeValue(&wrReqParam->handleValPair, 0, &cyBle_connHandle, CYBLE_GATT_DB_PEER_INITIATED);
                capsenseNotify = wrReqParam->handleValPair.value.val[0] & 0x01;
                CyBle_GattsWriteRsp(cyBle_connHandle);
            }		
            
			break;  

            
        /*  For MTU Exchange */    
        case CYBLE_EVT_GAP_DEVICE_CONNECTED:
            /* Once the devices are connected, the Client will not do a service
             * discovery and will assume that the handles of the Server are 
             * known. This is because discovery of custom service is not a part
             * of the BLE component today and will be in the next upgrade to BLE
             * component. 
             */
            
            /* Initiate an MTU exchange request */
            CyBle_GattcExchangeMtuReq(cyBle_connHandle, CYBLE_GATT_MTU);
         
            break;            

        case CYBLE_EVT_GATTC_XCHNG_MTU_RSP:
            /* Enable notifications on the characteristic to get data from the 
             * Server.
             */
            CyBle_GattcWriteCharacteristicDescriptors(cyBle_connHandle, &writeRequestData);
            break;
            
        case CYBLE_EVT_GATTS_XCNHG_MTU_REQ:

            CyBle_GattsExchangeMtuRsp(cyBle_connHandle, CYBLE_GATT_MTU);

            break;            
            
            
        default:
            break;
    }
} 

/***************************************************************
 * Main
 **************************************************************/
int main()
{
    CyGlobalIntEnable; 
    
    capsense_Start();
    capsense_InitializeEnabledBaselines();
    
    /* Start BLE stack and register the callback function */
    CyBle_Start(BleCallBack);
    
    
    /* StartConvert() must be called to initiate 
       conversion in Free Running Mode */
    ADC_Start();
    ADC_StartConvert();

    for(;;)
    {        
        /* if Capsense scan is done, read the value and start another scan */
//        if(!capsense_IsBusy())
//        {
//            fingerPos=capsense_GetCentroidPos(capsense_LINEARSLIDER0__LS);
//            analogIn = ADC_GetResult16(0u);
//            
//            capsense_UpdateEnabledBaselines();
//            capsense_ScanEnabledWidgets();
//            updateCapsense();
//            updateAnalogIn();
//
//        }
        if(!capsense_IsBusy())
        {
            fingerPos=capsense_GetCentroidPos(capsense_LINEARSLIDER0__LS);
            
            for ( a = 0 ; a <nSamples; a=a+1)
                analogIn[a] = ADC_GetResult16(0u);
                
            capsense_UpdateEnabledBaselines();
            capsense_ScanEnabledWidgets();
            updateCapsense();
            updateAnalogIn();

        }
        
        
        CyBle_ProcessEvents();
        CyBle_EnterLPM(CYBLE_BLESS_DEEPSLEEP);    
    }
}
