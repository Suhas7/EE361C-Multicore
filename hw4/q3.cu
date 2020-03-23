#include <stdio.h>
#include <string.h>
#define  tpb 32
//todo parallelize
__global__ void upSweep(int* arr, int* len, int* step){
    int index=threadIdx.x + blockIdx.x*tpb;
    if(((index+1)%(step*2)!=0) || index==0) return;
    arr[index]=arr[index]+arr[index-step];
}
__global__ void downSweep(int* arr, int* len, int* step){
    int index=threadIdx.x + blockIdx.x*tpb;
    if(((index+1)%(step*2)!=0) || index==0) return;
    arr[index]=arr[index]+arr[index-step];
}

__global__ void prefixSum(int* inp, int* inpLen, int* res, int* resLen){
    int runningTotal=0;
    int length=*inpLen;
    for(int i=0; i<length;i++){
        if((inp[i]%2)==1) runningTotal++;
        res[i]=runningTotal;
    }:
    *resLen=runningTotal;
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

void driver(int* cudaInp,int inpLen,int* cudInpLen, int* resLen){
    int* prefix;
    cudaMalloc((void**)&prefix, inpLen*sizeof(int));
    //compute prefixSum
    prefixSum<<<1,1>>>(cudaInp, cudInpLen, prefix,resLen);
    //alloc
    int outLen;
    cudaMemcpy(&outLen,resLen,sizeof(int),cudaMemcpyDeviceToHost);
    int* cudOut;
    cudaMalloc((void**) &cudOut, outLen*sizeof(int));
    //postprocess to make an array of odds
    copyOddsP<<<(inpLen+tpb)/tpb,tpb>>>(cudaInp, prefix, cudInpLen,cudOut);
    //print output
    int out[outLen];
    cudaMemcpy(out,cudOut, outLen*sizeof(int),cudaMemcpyDeviceToHost);
    for(int i=0; i<outLen; i++){
        printf("%d\n",out[i]);
    }
    cudaFree(prefix);
}

int main(int argc,char **argv){
    //read array in
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
    //GPU data transfer
    int* cudaInp;
    cudaMalloc((void**)&cudaInp,numLen*sizeof(int));
    cudaMemcpy(cudaInp, inp, numLen*sizeof(int), cudaMemcpyHostToDevice);
    int* inpLen;
    cudaMalloc((void**)&inpLen,sizeof(int));
    cudaMemcpy(inpLen,&numLen,sizeof(int),cudaMemcpyHostToDevice);
    int* resLen;
    cudaMalloc((void**)&resLen,sizeof(int));
    
    //run kernel
    driver(cudaInp,numLen, inpLen, resLen);
    cudaDeviceSynchronize();

    //recover data
    int resLenHost=7;
    cudaMemcpy(&resLenHost,resLen,sizeof(int),cudaMemcpyDeviceToHost);
    
    cudaFree(cudaInp);
    cudaFree(inpLen);
    cudaFree(resLen);
}
