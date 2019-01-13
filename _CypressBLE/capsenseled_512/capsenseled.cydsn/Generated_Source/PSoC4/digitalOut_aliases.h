/*******************************************************************************
* File Name: digitalOut.h  
* Version 2.20
*
* Description:
*  This file contains the Alias definitions for Per-Pin APIs in cypins.h. 
*  Information on using these APIs can be found in the System Reference Guide.
*
* Note:
*
********************************************************************************
* Copyright 2008-2015, Cypress Semiconductor Corporation.  All rights reserved.
* You may use this file only in accordance with the license, terms, conditions, 
* disclaimers, and limitations in the end user license agreement accompanying 
* the software package with which this file was provided.
*******************************************************************************/

#if !defined(CY_PINS_digitalOut_ALIASES_H) /* Pins digitalOut_ALIASES_H */
#define CY_PINS_digitalOut_ALIASES_H

#include "cytypes.h"
#include "cyfitter.h"
#include "cypins.h"


/***************************************
*              Constants        
***************************************/
#define digitalOut_0			(digitalOut__0__PC)
#define digitalOut_0_PS		(digitalOut__0__PS)
#define digitalOut_0_PC		(digitalOut__0__PC)
#define digitalOut_0_DR		(digitalOut__0__DR)
#define digitalOut_0_SHIFT	(digitalOut__0__SHIFT)
#define digitalOut_0_INTR	((uint16)((uint16)0x0003u << (digitalOut__0__SHIFT*2u)))

#define digitalOut_INTR_ALL	 ((uint16)(digitalOut_0_INTR))


#endif /* End Pins digitalOut_ALIASES_H */


/* [] END OF FILE */
