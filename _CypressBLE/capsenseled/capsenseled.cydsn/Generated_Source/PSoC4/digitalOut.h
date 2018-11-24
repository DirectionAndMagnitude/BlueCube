/*******************************************************************************
* File Name: digitalOut.h  
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

#if !defined(CY_PINS_digitalOut_H) /* Pins digitalOut_H */
#define CY_PINS_digitalOut_H

#include "cytypes.h"
#include "cyfitter.h"
#include "digitalOut_aliases.h"


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
} digitalOut_BACKUP_STRUCT;

/** @} structures */


/***************************************
*        Function Prototypes             
***************************************/
/**
* \addtogroup group_general
* @{
*/
uint8   digitalOut_Read(void);
void    digitalOut_Write(uint8 value);
uint8   digitalOut_ReadDataReg(void);
#if defined(digitalOut__PC) || (CY_PSOC4_4200L) 
    void    digitalOut_SetDriveMode(uint8 mode);
#endif
void    digitalOut_SetInterruptMode(uint16 position, uint16 mode);
uint8   digitalOut_ClearInterrupt(void);
/** @} general */

/**
* \addtogroup group_power
* @{
*/
void digitalOut_Sleep(void); 
void digitalOut_Wakeup(void);
/** @} power */


/***************************************
*           API Constants        
***************************************/
#if defined(digitalOut__PC) || (CY_PSOC4_4200L) 
    /* Drive Modes */
    #define digitalOut_DRIVE_MODE_BITS        (3)
    #define digitalOut_DRIVE_MODE_IND_MASK    (0xFFFFFFFFu >> (32 - digitalOut_DRIVE_MODE_BITS))

    /**
    * \addtogroup group_constants
    * @{
    */
        /** \addtogroup driveMode Drive mode constants
         * \brief Constants to be passed as "mode" parameter in the digitalOut_SetDriveMode() function.
         *  @{
         */
        #define digitalOut_DM_ALG_HIZ         (0x00u) /**< \brief High Impedance Analog   */
        #define digitalOut_DM_DIG_HIZ         (0x01u) /**< \brief High Impedance Digital  */
        #define digitalOut_DM_RES_UP          (0x02u) /**< \brief Resistive Pull Up       */
        #define digitalOut_DM_RES_DWN         (0x03u) /**< \brief Resistive Pull Down     */
        #define digitalOut_DM_OD_LO           (0x04u) /**< \brief Open Drain, Drives Low  */
        #define digitalOut_DM_OD_HI           (0x05u) /**< \brief Open Drain, Drives High */
        #define digitalOut_DM_STRONG          (0x06u) /**< \brief Strong Drive            */
        #define digitalOut_DM_RES_UPDWN       (0x07u) /**< \brief Resistive Pull Up/Down  */
        /** @} driveMode */
    /** @} group_constants */
#endif

/* Digital Port Constants */
#define digitalOut_MASK               digitalOut__MASK
#define digitalOut_SHIFT              digitalOut__SHIFT
#define digitalOut_WIDTH              1u

/**
* \addtogroup group_constants
* @{
*/
    /** \addtogroup intrMode Interrupt constants
     * \brief Constants to be passed as "mode" parameter in digitalOut_SetInterruptMode() function.
     *  @{
     */
        #define digitalOut_INTR_NONE      ((uint16)(0x0000u)) /**< \brief Disabled             */
        #define digitalOut_INTR_RISING    ((uint16)(0x5555u)) /**< \brief Rising edge trigger  */
        #define digitalOut_INTR_FALLING   ((uint16)(0xaaaau)) /**< \brief Falling edge trigger */
        #define digitalOut_INTR_BOTH      ((uint16)(0xffffu)) /**< \brief Both edge trigger    */
    /** @} intrMode */
/** @} group_constants */

/* SIO LPM definition */
#if defined(digitalOut__SIO)
    #define digitalOut_SIO_LPM_MASK       (0x03u)
#endif

/* USBIO definitions */
#if !defined(digitalOut__PC) && (CY_PSOC4_4200L)
    #define digitalOut_USBIO_ENABLE               ((uint32)0x80000000u)
    #define digitalOut_USBIO_DISABLE              ((uint32)(~digitalOut_USBIO_ENABLE))
    #define digitalOut_USBIO_SUSPEND_SHIFT        CYFLD_USBDEVv2_USB_SUSPEND__OFFSET
    #define digitalOut_USBIO_SUSPEND_DEL_SHIFT    CYFLD_USBDEVv2_USB_SUSPEND_DEL__OFFSET
    #define digitalOut_USBIO_ENTER_SLEEP          ((uint32)((1u << digitalOut_USBIO_SUSPEND_SHIFT) \
                                                        | (1u << digitalOut_USBIO_SUSPEND_DEL_SHIFT)))
    #define digitalOut_USBIO_EXIT_SLEEP_PH1       ((uint32)~((uint32)(1u << digitalOut_USBIO_SUSPEND_SHIFT)))
    #define digitalOut_USBIO_EXIT_SLEEP_PH2       ((uint32)~((uint32)(1u << digitalOut_USBIO_SUSPEND_DEL_SHIFT)))
    #define digitalOut_USBIO_CR1_OFF              ((uint32)0xfffffffeu)
#endif


/***************************************
*             Registers        
***************************************/
/* Main Port Registers */
#if defined(digitalOut__PC)
    /* Port Configuration */
    #define digitalOut_PC                 (* (reg32 *) digitalOut__PC)
#endif
/* Pin State */
#define digitalOut_PS                     (* (reg32 *) digitalOut__PS)
/* Data Register */
#define digitalOut_DR                     (* (reg32 *) digitalOut__DR)
/* Input Buffer Disable Override */
#define digitalOut_INP_DIS                (* (reg32 *) digitalOut__PC2)

/* Interrupt configuration Registers */
#define digitalOut_INTCFG                 (* (reg32 *) digitalOut__INTCFG)
#define digitalOut_INTSTAT                (* (reg32 *) digitalOut__INTSTAT)

/* "Interrupt cause" register for Combined Port Interrupt (AllPortInt) in GSRef component */
#if defined (CYREG_GPIO_INTR_CAUSE)
    #define digitalOut_INTR_CAUSE         (* (reg32 *) CYREG_GPIO_INTR_CAUSE)
#endif

/* SIO register */
#if defined(digitalOut__SIO)
    #define digitalOut_SIO_REG            (* (reg32 *) digitalOut__SIO)
#endif /* (digitalOut__SIO_CFG) */

/* USBIO registers */
#if !defined(digitalOut__PC) && (CY_PSOC4_4200L)
    #define digitalOut_USB_POWER_REG       (* (reg32 *) CYREG_USBDEVv2_USB_POWER_CTRL)
    #define digitalOut_CR1_REG             (* (reg32 *) CYREG_USBDEVv2_CR1)
    #define digitalOut_USBIO_CTRL_REG      (* (reg32 *) CYREG_USBDEVv2_USB_USBIO_CTRL)
#endif    
    
    
/***************************************
* The following code is DEPRECATED and 
* must not be used in new designs.
***************************************/
/**
* \addtogroup group_deprecated
* @{
*/
#define digitalOut_DRIVE_MODE_SHIFT       (0x00u)
#define digitalOut_DRIVE_MODE_MASK        (0x07u << digitalOut_DRIVE_MODE_SHIFT)
/** @} deprecated */

#endif /* End Pins digitalOut_H */


/* [] END OF FILE */
