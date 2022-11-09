#include <HX711.h>
#include <EEPROM.h>

// #include "HX711.h"

const int LOADCELL_DOUT_PIN=4;
const int LOADCELL_SCK_PIN=5;
int addr=0;
HX711 scale;
void setup() {
  // put your setup code here, to run once:
  Serial.begin(57600);
  // ble_device.begin(57600);
  scale.begin(LOADCELL_DOUT_PIN,LOADCELL_SCK_PIN);
  if(scale.is_ready())
  {
    scale.set_scale();
    Serial.println("Tare... remove any weights from the scale.");
    delay(5000);
    scale.tare();
    Serial.println("Tare done...");
    Serial.print("Place a known weight on the scale...");
    delay(15000);
    long reading = scale.get_units(20);
    Serial.print("Result: ");
    Serial.println(reading);    
    Serial.println("Enter the weight of object placed");
    while(Serial.available()==0){

    }
    float weight=Serial.parseFloat();
    float cal_val=reading/weight;
    Serial.print("Callibraion value is ");
    Serial.println(cal_val);
    EEPROM.write(addr,cal_val);
    Serial.println("Value written to EEPROM");    
  }
  else{
    Serial.println("No HX711 Amplifier Set");
  }  
}
void loop(){}


