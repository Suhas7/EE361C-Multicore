#include <stdio.h>
#include <string.h>
#define  tpb 32

__global__ void oddCheck(int* nums,int*len, int* out, int* last){
    int index=threadIdx.x + blockIdx.x*tpb;
    if (index<*len) out[index]=nums[index]%2;
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
    for(int i=0;i<((*len)-1);i++) printf("%d, ",arr[i]);
    printf("%d",arr[(*len)-1]);
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
    int* nums = inp;
    int falseLen=1;
    while(falseLen<numLen) falseLen*=2;
    int Len=falseLen;
    int* cudLen;
    cudaMalloc(&cudLen,sizeof(int));
    cudaMemcpy(cudLen,&Len,sizeof(int),cudaMemcpyHostToDevice);
    int* trueLen;
    cudaMalloc(&trueLen,sizeof(int));
    cudaMemcpy(trueLen,&numLen,sizeof(int),cudaMemcpyHostToDevice);
    int* cudNum;
    cudaMalloc(&cudNum,(Len)*sizeof(int));
    cudaMemcpy(cudNum,nums,(Len)*sizeof(int),cudaMemcpyHostToDevice);
    int* out;
    cudaMalloc(&out,(Len+1)*sizeof(int));
    int* last;
    cudaMalloc(&last,sizeof(int));
    oddCheck<<<(Len+tpb)/tpb,tpb>>>(cudNum,cudLen,out,last);
    for(int step=1; step<Len; step*=2){
        upSweep<<<(Len+tpb)/tpb,tpb>>>(out,cudLen,trueLen,step);
    }
    for(int step=Len/2; step>0; step/=2){
        downSweep<<<(Len+tpb)/tpb,tpb>>>(out,cudLen,trueLen,step);
    }
    Len=numLen;
    cudLen=trueLen;
    int* shifted;
    cudaMalloc(&shifted,Len*sizeof(int));
    exToIn<<<(Len+tpb)/tpb,tpb>>>(out,shifted,cudLen,last);
    int* cudOut;
    cudaMalloc((void**) &cudOut, Len*sizeof(int));
    copyOddsP<<<(Len+tpb)/tpb,tpb>>>(cudNum, shifted, cudLen,cudOut); 
    printArr<<<1,1>>>(cudOut,last);
    cudaFree(cudLen);
    cudaFree(cudNum);
    cudaFree(out);
    cudaFree(last);
    cudaFree(shifted);
    cudaFree(cudOut);
    return 0;
}
