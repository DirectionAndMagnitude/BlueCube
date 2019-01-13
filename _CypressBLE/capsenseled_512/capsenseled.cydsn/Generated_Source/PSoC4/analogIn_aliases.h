/*******************************************************************************
* File Name: analogIn.h  
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

#if !defined(CY_PINS_analogIn_ALIASES_H) /* Pins analogIn_ALIASES_H */
#define CY_PINS_analogIn_ALIASES_H

#include "cytypes.h"
#include "cyfitter.h"
#include "cypins.h"


/***************************************
*              Constants        
***************************************/
#define analogIn_0			(analogIn__0__PC)
#define analogIn_0_PS		(analogIn__0__PS)
#define analogIn_0_PC		(analogIn__0__PC)
#define analogIn_0_DR		(analogIn__0__DR)
#define analogIn_0_SHIFT	(analogIn__0__SHIFT)
#define analogIn_0_INTR	((uint16)((uint16)0x0003u << (analogIn__0__SHIFT*2u)))

#define analogIn_INTR_ALL	 ((uint16)(analogIn_0_INTR))


#endif /* End Pins analogIn_ALIASES_H */


/* [] END OF FILE */
