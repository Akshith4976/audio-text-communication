#define MIC_PIN A0

#define START_FREQ   1000.0
#define BIN_WIDTH    10.0
#define TOTAL_BINS   27        
#define SYMBOL_TIME  500       

unsigned long lastCross = 0;

float freqSum = 0;
int freqCount = 0;

int minVal = 1023;
int maxVal = 0;

String decodedText = "";

void setup() {
  Serial.begin(115200);
  delay(1000);
  Serial.println("Sound → Plain Text Decoder");
  Serial.println("Play tones, one character every 500 ms");
}

void loop() {
  unsigned long startTime = millis();

  freqSum = 0;
  freqCount = 0;
  minVal = 1023;
  maxVal = 0;
  lastCross = 0;


  while (millis() - startTime < SYMBOL_TIME) {
    int s = analogRead(MIC_PIN);

    if (s < minVal) minVal = s;
    if (s > maxVal) maxVal = s;

    int midpoint = (minVal + maxVal) / 2;
    static int lastS = midpoint;

    if (lastS < midpoint && s >= midpoint) {
      unsigned long now = micros();

      if (lastCross > 0) {
        float freq = 1000000.0 / (now - lastCross);

        if (freq >= START_FREQ &&
            freq <= START_FREQ + BIN_WIDTH * TOTAL_BINS) {
          freqSum += freq;
          freqCount++;
        }
      }
      lastCross = now;
    }

    lastS = s;
  }

 
  if (freqCount > 0) {
    float avgFreq = freqSum / freqCount;
    char c = freqToChar(avgFreq);

    if (c != '?') {
      decodedText += c;
      Serial.print(decodedText);   
      Serial.println();
    }
  }

  delay(500);  
}


char freqToChar(float freq) {
  if (freq < START_FREQ) return '?';

  int index = (int)((freq - START_FREQ) / BIN_WIDTH);

  if (index >= 0 && index < 26) {
    return 'A' + index;
  }

  if (index == 26) {
    return ' ';
  }

  return '?';
}
