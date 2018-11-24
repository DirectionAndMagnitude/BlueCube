/*******************************************************************************
* File Name: analogIn.h  
* Version 2.20
*
* Description:
*  This file contains Pin function prototypes and register defines
*
********************************************************************************
* Copyright 2008-2015, Cypress Semiconductor Corporation.  All rights reserved.
* You may use this file only in accordance with the license, terms, conditions, 
* disclaimers, and limitations in the end user license agreement accompanying 
* the software package with which this file was provided.
*******************************************************************************/

#if !defined(CY_PINS_analogIn_H) /* Pins analogIn_H */
#define CY_PINS_analogIn_H

#include "cytypes.h"
#include "cyfitter.h"
#include "analogIn_aliases.h"


/***************************************
*     Data Struct Definitions
***************************************/

/**
* \addtogroup group_structures
* @{
*/
    
/* Structure for sleep mode support */
typedef struct
{
    uint32 pcState; /**< State of the port control register */
    uint32 sioState; /**< State of the SIO configuration */
    uint32 usbState; /**< State of the USBIO regulator */
} analogIn_BACKUP_STRUCT;

/** @} structures */


/***************************************
*        Function Prototypes             
***************************************/
/**
* \addtogroup group_general
* @{
*/
uint8   analogIn_Read(void);
void    analogIn_Write(uint8 value);
uint8   analogIn_ReadDataReg(void);
#if defined(analogIn__PC) || (CY_PSOC4_4200L) 
    void    analogIn_SetDriveMode(uint8 mode);
#endif
void    analogIn_SetInterruptMode(uint16 position, uint16 mode);
uint8   analogIn_ClearInterrupt(void);
/** @} general */

/**
* \addtogroup group_power
* @{
*/
void analogIn_Sleep(void); 
void analogIn_Wakeup(void);
/** @} power */


/***************************************
*           API Constants        
***************************************/
#if defined(analogIn__PC) || (CY_PSOC4_4200L) 
    /* Drive Modes */
    #define analogIn_DRIVE_MODE_BITS        (3)
    #define analogIn_DRIVE_MODE_IND_MASK    (0xFFFFFFFFu >> (32 - analogIn_DRIVE_MODE_BITS))

    /**
    * \addtogroup group_constants
    * @{
    */
        /** \addtogroup driveMode Drive mode constants
         * \brief Constants to be passed as "mode" parameter in the analogIn_SetDriveMode() function.
         *  @{
         */
        #define analogIn_DM_ALG_HIZ         (0x00u) /**< \brief High Impedance Analog   */
        #define analogIn_DM_DIG_HIZ         (0x01u) /**< \brief High Impedance Digital  */
        #define analogIn_DM_RES_UP          (0x02u) /**< \brief Resistive Pull Up       */
        #define analogIn_DM_RES_DWN         (0x03u) /**< \brief Resistive Pull Down     */
        #define analogIn_DM_OD_LO           (0x04u) /**< \brief Open Drain, Drives Low  */
        #define analogIn_DM_OD_HI           (0x05u) /**< \brief Open Drain, Drives High */
        #define analogIn_DM_STRONG          (0x06u) /**< \brief Strong Drive            */
        #define analogIn_DM_RES_UPDWN       (0x07u) /**< \brief Resistive Pull Up/Down  */
        /** @} driveMode */
    /** @} group_constants */
#endif

/* Digital Port Constants */
#define analogIn_MASK               analogIn__MASK
#define analogIn_SHIFT              analogIn__SHIFT
#define analogIn_WIDTH              1u

/**
* \addtogroup group_constants
* @{
*/
    /** \addtogroup intrMode Interrupt constants
     * \brief Constants to be passed as "mode" parameter in analogIn_SetInterruptMode() function.
     *  @{
     */
        #define analogIn_INTR_NONE      ((uint16)(0x0000u)) /**< \brief Disabled             */
        #define analogIn_INTR_RISING    ((uint16)(0x5555u)) /**< \brief Rising edge trigger  */
        #define analogIn_INTR_FALLING   ((uint16)(0xaaaau)) /**< \brief Falling edge trigger */
        #define analogIn_INTR_BOTH      ((uint16)(0xffffu)) /**< \brief Both edge trigger    */
    /** @} intrMode */
/** @} group_constants */

/* SIO LPM definition */
#if defined(analogIn__SIO)
    #define analogIn_SIO_LPM_MASK       (0x03u)
#endif

/* USBIO definitions */
#if !defined(analogIn__PC) && (CY_PSOC4_4200L)
    #define analogIn_USBIO_ENABLE               ((uint32)0x80000000u)
    #define analogIn_USBIO_DISABLE              ((uint32)(~analogIn_USBIO_ENABLE))
    #define analogIn_USBIO_SUSPEND_SHIFT        CYFLD_USBDEVv2_USB_SUSPEND__OFFSET
    #define analogIn_USBIO_SUSPEND_DEL_SHIFT    CYFLD_USBDEVv2_USB_SUSPEND_DEL__OFFSET
    #define analogIn_USBIO_ENTER_SLEEP          ((uint32)((1u << analogIn_USBIO_SUSPEND_SHIFT) \
                                                        | (1u << analogIn_USBIO_SUSPEND_DEL_SHIFT)))
    #define analogIn_USBIO_EXIT_SLEEP_PH1       ((uint32)~((uint32)(1u << analogIn_USBIO_SUSPEND_SHIFT)))
    #define analogIn_USBIO_EXIT_SLEEP_PH2       ((uint32)~((uint32)(1u << analogIn_USBIO_SUSPEND_DEL_SHIFT)))
    #define analogIn_USBIO_CR1_OFF              ((uint32)0xfffffffeu)
#endif


/***************************************
*             Registers        
***************************************/
/* Main Port Registers */
#if defined(analogIn__PC)
    /* Port Configuration */
    #define analogIn_PC                 (* (reg32 *) analogIn__PC)
#endif
/* Pin State */
#define analogIn_PS                     (* (reg32 *) analogIn__PS)
/* Data Register */
#define analogIn_DR                     (* (reg32 *) analogIn__DR)
/* Input Buffer Disable Override */
#define analogIn_INP_DIS                (* (reg32 *) analogIn__PC2)

/* Interrupt configuration Registers */
#define analogIn_INTCFG                 (* (reg32 *) analogIn__INTCFG)
#define analogIn_INTSTAT                (* (reg32 *) analogIn__INTSTAT)

/* "Interrupt cause" register for Combined Port Interrupt (AllPortInt) in GSRef component */
#if defined (CYREG_GPIO_INTR_CAUSE)
    #define analogIn_INTR_CAUSE         (* (reg32 *) CYREG_GPIO_INTR_CAUSE)
#endif

/* SIO register */
#if defined(analogIn__SIO)
    #define analogIn_SIO_REG            (* (reg32 *) analogIn__SIO)
#endif /* (analogIn__SIO_CFG) */

/* USBIO registers */
#if !defined(analogIn__PC) && (CY_PSOC4_4200L)
    #define analogIn_USB_POWER_REG       (* (reg32 *) CYREG_USBDEVv2_USB_POWER_CTRL)
    #define analogIn_CR1_REG             (* (reg32 *) CYREG_USBDEVv2_CR1)
    #define analogIn_USBIO_CTRL_REG      (* (reg32 *) CYREG_USBDEVv2_USB_USBIO_CTRL)
#endif    
    
    
/***************************************
* The following code is DEPRECATED and 
* must not be used in new designs.
***************************************/
/**
* \addtogroup group_deprecated
* @{
*/
#define analogIn_DRIVE_MODE_SHIFT       (0x00u)
#define analogIn_DRIVE_MODE_MASK        (0x07u << analogIn_DRIVE_MODE_SHIFT)
/** @} deprecated */

#endif /* End Pins analogIn_H */


/* [] END OF FILE */
