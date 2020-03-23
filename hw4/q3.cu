#include <stdio.h>
#include <string.h>
#define  tpb 32

__global__ void oddCheck(int* nums,int*len, int* out){
    int index=threadIdx.x + blockIdx.x*tpb;
    if (index<*len){ out[index]=nums[index]%2; }
}

//todo validate this
__global__ void upSweep(int* arr, int* len, int step){
    int index=threadIdx.x + blockIdx.x*tpb;
    if((((index+1)%(step*2))!=0) || index==0) return;
    arr[index]=arr[index]+arr[index-step];
}

//todo validate this
__global__ void downSweep(int* arr, int* len, int step){
    int index=threadIdx.x + blockIdx.x*tpb;
    if((((index+1)%(step*2))!=0) || index==0) return;
    int tmp=arr[index-step];
    arr[index-step]=arr[index];
    arr[index]+=tmp;
}

__global__ void printArr(int* arr,int*len){
    for(int i=0;i<(*len);i++) printf("%d",arr[i]);
}

void prefixSumP(int* inp, int* inpLen, int* res, int* resLen){
    oddCheck<<<((*inpLen)+tpb)/tpb,tpb>>>(inp,inpLen,res);
    for(int step=1; step<*inpLen; step*=2){
        upSweep<<<((*inpLen)+tpb)/tpb,tpb>>>(res,inpLen,step);
    }
    //res[(*inpLen)-1]=0;
    for(int step=*inpLen; step>0; step/=2){
        downSweep<<<((*inpLen)+tpb)/tpb,tpb>>>(res,inpLen,step);
    }
    printf("kernel print start");
    printArr<<<1,1>>>(res,inpLen);
    printf("kernel print end");
    *resLen=res[(*inpLen)-1]+(inp[(*inpLen)-1]%2);
}

__global__ void prefixSum(int* inp, int* inpLen, int* res, int* resLen){
    int runningTotal=0;
    int length=*inpLen;
    for(int i=0; i<length;i++){
        if((inp[i]%2)==1) runningTotal++;
        res[i]=runningTotal;
    }
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

int main2(int argc,char **argv){
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

int main(int argc,char **argv){
    int nums[8]= {1,2,3,4,5,6,7,8};
    int Len=8;
    int* cudLen;
    cudaMalloc(&cudLen,sizeof(int));
    cudaMemcpy(cudLen,Len,sizeof(int),cudaMemcpyHostToDevice);
    int* cudNum;
    cudaMalloc(&cudNum,8*sizeof(int));
    cudaMemcpy(cudNum,nums,8*sizeof(int),cudaMemcpyHostToDevice);

    upSweep<<<2,4>>>(cudNum, cudLen, 1);
    printArr(cudNum,cudLen);
}