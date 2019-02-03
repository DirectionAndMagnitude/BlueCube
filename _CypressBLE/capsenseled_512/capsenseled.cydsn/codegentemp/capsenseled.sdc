# THIS FILE IS AUTOMATICALLY GENERATED
# Project: C:\Users\jerome.crocco\Documents\GitHub\BlueCube\_CypressBLE\capsenseled_512\capsenseled.cydsn\capsenseled.cyprj
# Date: Sat, 02 Feb 2019 21:03:23 GMT
#set_units -time ns
create_clock -name {capsense_SampleClk(FFB)} -period 5312.5 -waveform {0 2656.25} [list [get_pins {ClockBlock/ff_div_5}]]
create_clock -name {capsense_SenseClk(FFB)} -period 5312.5 -waveform {0 2656.25} [list [get_pins {ClockBlock/ff_div_4}]]
create_clock -name {ADC_intClock(FFB)} -period 437.5 -waveform {0 218.75} [list [get_pins {ClockBlock/ff_div_6}]]
create_clock -name {CyRouted1} -period 20.833333333333332 -waveform {0 10.4166666666667} [list [get_pins {ClockBlock/dsi_in_0}]]
create_clock -name {CyWCO} -period 30517.578125 -waveform {0 15258.7890625} [list [get_pins {ClockBlock/wco}]]
create_clock -name {CyLFCLK} -period 30517.578125 -waveform {0 15258.7890625} [list [get_pins {ClockBlock/lfclk}]]
create_clock -name {CyILO} -period 31250 -waveform {0 15625} [list [get_pins {ClockBlock/ilo}]]
create_clock -name {CyECO} -period 41.666666666666664 -waveform {0 20.8333333333333} [list [get_pins {ClockBlock/eco}]]
create_clock -name {CyIMO} -period 20.833333333333332 -waveform {0 10.4166666666667} [list [get_pins {ClockBlock/imo}]]
create_clock -name {CyHFCLK} -period 20.833333333333332 -waveform {0 10.4166666666667} [list [get_pins {ClockBlock/hfclk}]]
create_clock -name {CySYSCLK} -period 20.833333333333332 -waveform {0 10.4166666666667} [list [get_pins {ClockBlock/sysclk}]]
create_generated_clock -name {capsense_SampleClk} -source [get_pins {ClockBlock/hfclk}] -edges {1 255 511} [list]
create_generated_clock -name {capsense_SenseClk} -source [get_pins {ClockBlock/hfclk}] -edges {1 255 511} [list]
create_generated_clock -name {ADC_intClock} -source [get_pins {ClockBlock/hfclk}] -edges {1 21 43} [list]
create_generated_clock -name {Clock_1} -source [get_pins {ClockBlock/hfclk}] -edges {1 48001 96001} [list [get_pins {ClockBlock/udb_div_0}]]

set_false_path -from [get_pins {__ONE__/q}]

# Component constraints for C:\Users\jerome.crocco\Documents\GitHub\BlueCube\_CypressBLE\capsenseled_512\capsenseled.cydsn\TopDesign\TopDesign.cysch
# Project: C:\Users\jerome.crocco\Documents\GitHub\BlueCube\_CypressBLE\capsenseled_512\capsenseled.cydsn\capsenseled.cyprj
# Date: Sat, 02 Feb 2019 21:03:20 GMT
