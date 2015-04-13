#include "J35DL.h";

#define A  0b10001001 
#define B  0b10001011 
#define C  0b10001101 
#define D  0b10001111 
#define E  0b10010001 
#define F  0b10010011 
#define G  0b10010101 
#define H  0b10010111 
#define I  0b10011001 
#define J  0b10011011 
#define K  0b10011101 
#define L  0b10011111 
#define M  0b10100011 
#define N  0b10100101 
#define O 0b10100111 
#define P  0b10101001 
#define Q  0b10101011 
#define R  0b10101101 
#define S  0b10101111 
#define T  0b10110001 
#define U  0b10110011 
#define V  0b10110101 
#define W  0b10110111 
#define X  0b10111001 
#define Y  0b10111011 
#define Z  0b10111101 
#define COMMA  0b10111111 
#define OPEN_BRACKET  0b11000101 
#define DOT  0b11000111 
#define PLUS  0b11001001 
#define SPACE  0b11001011 
#define MORE  0b11001101 
#define DOLLAR  0b11001111 
#define DOUBLEDOT  0b11010001 
#define DIVIDE  0b11010011 
#define NUM_1  0b11010101 
#define NUM_3 0b11010111 
#define NUM_5 0b11011001 
#define NUM_7 0b11011011 
#define NUM_9 0b11011101 
#define CLOSE_BRACKET 0b11011111 
#define MINUS 0b11100011 
#define EQUALS 0b11100101 
#define LESS 0b11100111 
#define PERCENT 0b11101001 
#define MULTIPLY 0b11101011 
#define NUM_0 0b11101101 
#define NUM_2 0b11101111 
#define NUM_4 0b11110001 
#define NUM_6 0b11110011 
#define NUM_8 0b11110101 
#define NEXTLINE 0b11110111 
#define ASK 0b11111001 
#define LOUD 0b11111011 
#define NUM 0b11111101 
#define DOGE 0b11111111 

byte trasnlateIntToSymbol(char c) {
  if (c=='A' || c=='a') return (A);
  else
    if (c=='B' || c=='b') return(B);
    else
      if (c=='C' || c=='c') return(C);
      else
        if (c=='D' || c=='d') return(D);
        else
          if (c=='E' || c=='e') return(E);
          else
            if (c=='F' || c=='f') return(F);
            else
              if (c=='G' || c=='g') return(G);
              else
                if (c=='H' || c=='h') return(H);
                else
                  if (c=='I' || c=='i') return(I);
                  else
                    if (c=='J' || c=='j') return(J);
                    else
                      if (c=='K' || c=='k') return(K);
                      else
                        if (c=='L' || c=='l') return(L);
                        else
                          if (c=='M' || c=='m') return(M);
                          else
                            if (c=='N' || c=='n') return(N);
                            else
                              if (c=='O' || c=='o') return(O);
                              else
                                if (c=='P' || c=='p') return(P);
                                else
                                  if (c=='Q' || c=='q') return (Q);
                                  else
                                    if (c=='R' || c=='r') return (R);
                                    else
                                      if (c=='S' || c=='s') return (S);
                                      else
                                        if (c=='T' || c=='t') return (T);
                                        else
                                          if (c=='U' || c=='u') return (U);
                                          else
                                            if (c=='V' || c=='v') return (V);
                                            else
                                              if (c=='W' || c=='w') return (W);
                                              else
                                                if (c=='X' || c=='x') return (X);
                                                else
                                                  if (c=='Y' || c=='y') return (Y);
                                                  else
                                                    if (c=='Z' || c=='z') return (Z);
                                                      else
                                                        if (c==',') return (COMMA ); 
                                                        else
                                                          if (c=='(') return (OPEN_BRACKET ); 
                                                          else
                                                            if (c=='.') return (DOT ); 
                                                            else
                                                              if (c=='+') return (PLUS ); 
                                                              else
                                                                if (c==' ') return (SPACE ); 
                                                                else
                                                                  if (c=='>') return (MORE ); 
                                                                  else
                                                                    if (c=='$') return (DOLLAR ); 
                                                                    else
                                                                      if (c==':') return (DOUBLEDOT ); 
                                                                      else
                                                                        if (c=='/') return (DIVIDE ); 
                                                                        else
                                                                          if (c=='1') return (NUM_1 ); 
                                                                          else
                                                                            if (c=='3') return (NUM_3); 
                                                                            else
                                                                              if (c=='5') return (NUM_5); 
                                                                              else
                                                                                if (c=='7') return (NUM_7); 
                                                                                else
                                                                                  if (c=='9') return (NUM_9); 
                                                                                  else
                                                                                    if (c==')') return (CLOSE_BRACKET); 
                                                                                    else
                                                                                      if (c=='-') return (MINUS); 
                                                                                      else
                                                                                        if (c=='=') return (EQUALS); 
                                                                                        else
                                                                                          if (c=='<') return (LESS); 
                                                                                          else
                                                                                            if (c=='%') return (PERCENT); 
                                                                                            else
                                                                                              if (c=='*') return (MULTIPLY); 
                                                                                              else
                                                                                                if (c=='0') return (NUM_0); 
                                                                                                else
                                                                                                  if (c=='2') return (NUM_2); 
                                                                                                  else
                                                                                                    if (c=='4') return (NUM_4); 
                                                                                                    else
                                                                                                      if (c=='6') return (NUM_6); 
                                                                                                      else
                                                                                                        if (c=='8') return (NUM_8); 
                                                                                                        else
                                                                                                          if (c=='\n') return (NEXTLINE); 
                                                                                                          else
                                                                                                            if (c=='?') return (ASK); 
                                                                                                            else
                                                                                                              if (c=='!') return (LOUD); 
                                                                                                              else
                                                                                                                if (c=='№' || c=='#') return (NUM); 
                                                                                                                else
                                                                                                                  if (c=='@') return (DOGE); 
                                                                                                                  else
                                                                                                                    return (SPACE);

}

byte trasnlateIntToSymbol(byte b) {
  switch (b) {
     case 0: return NUM_0; 
     case 1: return NUM_1; 
     case 2: return NUM_2; 
     case 3: return NUM_3; 
     case 4: return NUM_4; 
     case 5: return NUM_5; 
     case 6: return NUM_6; 
     case 7: return NUM_7; 
     case 8: return NUM_8; 
     case 9: return NUM_9;     
  }
  return SPACE;    
}

int dataPin;
int syncPin;

void initConnection(int sync, int data) {
  dataPin=data;
  syncPin=sync;
  pinMode(dataPin, OUTPUT);
  pinMode(syncPin, OUTPUT);
}

inline void tick() {
  delayMicroseconds(preSyncDelay);
  digitalWrite(syncPin, HIGH);
  delayMicroseconds(syncDelay);
  digitalWrite(syncPin, LOW);
  delayMicroseconds(syncDelay);
}

void startSending() {
  digitalWrite(dataPin, HIGH);
  tick();  
  digitalWrite(dataPin, LOW);  
  tick();
  tick();
  tick();
  tick();
}

void stopSending() {
  delay(afterMessageDelay);
}

void sendSymbol(char c) {
      for (int j=7; j>=0; j--) {
          digitalWrite(dataPin, bitRead(c, j));
          tick();
      }   
}

void sendText(char msg) {  
      sendSymbol(trasnlateIntToSymbol(msg));
}

void sendText(char* msg) {  
  int j=0;
  while (msg[j]!=0) { 
    sendSymbol(trasnlateIntToSymbol(msg[j]));
    j++;
  }
}

void sendText(String msg) {  
  for (int j=0; j<msg.length(); j++)
    sendSymbol(trasnlateIntToSymbol(msg.charAt(j)));
}

void sendNumber(long value) {
  boolean isNegative=value<0;
  byte letters[17];
  long n=abs(value);
  byte last=16;
  for (int i=16; i>=0; i--) {
        
      if (n==0) {
          if (isNegative) {
            last=i;
            letters[last]=MINUS;
          } else {
            last=i+1;
          }
          break;
      }
    
      byte lastSymb=n%10;
      
      letters[i]=trasnlateIntToSymbol(lastSymb);
      
      n=n/10;  
  }
  
  if (last==17) {
    last=16;
    letters[16]=NUM_0;
  }
  for (int i=last; i<17; i++) {
    sendSymbol(letters[i]);
  }
}
