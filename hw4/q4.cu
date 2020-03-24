#include <stdio.h>
#include <string.h>
#define  tpb 32

__global__ void bitMask(int* nums,int*len, int* out, int* last, int*bit, int*value){
    int index=threadIdx.x + blockIdx.x*tpb;
    if (index<*len) out[index]=(((nums[index]>>(*bit))%2)==*value);
    if(index==((*len)-1)) *last=out[index];
}

__global__ void exToIn(int* inp, int* out, int*len, int*last){
    int index = threadIdx.x + blockIdx.x*tpb;
    if((index>0)&&(index<*len)){
        out[index-1]=inp[index];
    }
    if(index==((*len)-1)) { out[index]=inp[index]+*last;
    *last=out[index];
    }
}

__global__ void upSweep(int* arr, int* len, int* tLen, int step){
    int index=threadIdx.x + blockIdx.x*tpb;
    if(index>*tLen) return;
    if((((index+1)%(step*2))!=0) || index==0 || ((*len)<=index)) return;
    arr[index]=arr[index]+arr[index-step];
}

__global__ void downSweep(int* arr, int* len, int* tLen, int step){
    int index=threadIdx.x + blockIdx.x*tpb;
    if(2*step==*len) arr[(*len)-1]=0;
    if((((index+1)%(step*2))!=0) || (index==0) || ((*len)<=index)) return;    
    int tmp=arr[index-step];
    arr[index-step]=arr[index];
    arr[index]+=tmp;
}

__global__ void printArr(int* arr,int*len){
    //printf("%d",*len);
    for(int i=0;i<(*len);i++) printf("%d\n",arr[i]);
}

__global__ void copyOddsP(int*inp, int*prefix, int*inpLen,int*out){
    if((blockIdx.x+threadIdx.x)==0){ out[0]=inp[0];}
    else if((blockIdx.x+threadIdx.x)<*inpLen){
        int i=threadIdx.x + blockIdx.x*tpb;
        if(prefix[i]!=prefix[i-1]){
            out[prefix[i-1]]=inp[i];
        }
    }
}

int* filter(int* cudNum, int numLen, int bit, int value, int** zeroLen){
    int* cBit;
    int* cVal;
    cudaMalloc(&cBit,sizeof(int));
    cudaMalloc(&cVal,sizeof(int));
    cudaMemcpy(cBit,&bit,sizeof(int),cudaMemcpyHostToDevice);
    cudaMemcpy(cVal,&value,sizeof(int),cudaMemcpyHostToDevice);
    int falseLen=1;
    while(falseLen<numLen) falseLen*=2;
    int Len=falseLen;
    int* cudLen;
    cudaMalloc(&cudLen,sizeof(int));
    cudaMemcpy(cudLen,&Len,sizeof(int),cudaMemcpyHostToDevice);
    int* trueLen;
    cudaMalloc(&trueLen,sizeof(int));
    cudaMemcpy(trueLen,&numLen,sizeof(int),cudaMemcpyHostToDevice);
    //printArr<<<1,1>>>(cudNum,trueLen);
    int* out;
    cudaMalloc(&out,(Len+1)*sizeof(int));
    int* last;
    cudaMalloc(&last,sizeof(int));
    bitMask<<<(Len+tpb)/tpb,tpb>>>(cudNum,cudLen,out,last,cBit,cVal);
    for(int step=1; step<Len; step*=2){ upSweep<<<(Len+tpb)/tpb,tpb>>>(out,cudLen,trueLen,step); }
    for(int step=Len/2; step>0; step/=2){ downSweep<<<(Len+tpb)/tpb,tpb>>>(out,cudLen,trueLen,step); }
    Len=numLen;
    cudLen=trueLen;
    int* shifted;
    cudaMalloc(&shifted,Len*sizeof(int));
    exToIn<<<(Len+tpb)/tpb,tpb>>>(out,shifted,cudLen,last);
    int* cudOut;
    cudaMalloc((void**) &cudOut, Len*sizeof(int));
    copyOddsP<<<(Len+tpb)/tpb,tpb>>>(cudNum, shifted, cudLen,cudOut);   
    *zeroLen = last;
    //cudaFree(cudLen);
    //cudaFree(cudNum);
    //cudaFree(out);
    //cudaFree(last);
    //cudaFree(shifted);
    return cudOut;
}

__global__ void copyArr(int*a, int*b, int*c, int* lenB, int*lenC){
    int index=threadIdx.x + blockIdx.x*tpb;
    if(index>=((*lenB)+(*lenC))) return;
    if(index<(*lenB)) a[index]=b[index];
    else a[index]=c[index-(*lenB)];
}

int main(int argc,char **argv){
    char buff[50000];
    int inp[15000];
    buff[0]=' ';
    char* token;
    FILE* fp = fopen("inp.txt", "r" );
    fgets(buff+1, 50000, fp);
    token=strtok(buff,",");
    int numLen=0;
    while(token!=NULL){
        inp[numLen]=atoi(token+1);
        numLen++;
        token=strtok(NULL,",");
    }
    int* zerLen;
    int* oneLen;
    int* start;
    int* end; 
    int* cudLen;
    int maxBit=1;
    int* cudNum;
    cudaMalloc(&cudNum,(numLen*sizeof(int)));
    cudaMemcpy(cudNum,inp,(numLen)*sizeof(int),cudaMemcpyHostToDevice);
    while((1<<maxBit)<numLen) maxBit++;
    cudaMalloc(&cudLen,sizeof(int));
    cudaMemcpy(cudLen,&numLen,sizeof(int),cudaMemcpyHostToDevice); 
    for(int i=0; i<10; i++){
        start=filter(cudNum,numLen,i,0, &zerLen);
        end=filter(cudNum,numLen,i,1,&oneLen);
        copyArr<<<(numLen+tpb)/tpb,tpb>>>(cudNum,start,end,zerLen,oneLen);
    }
    cudaMemcpy(inp,cudNum,numLen*sizeof(int),cudaMemcpyDeviceToHost);
    for(int j=0; j<numLen; j++) printf("%d\n",inp[j]);
}
