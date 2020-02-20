#include <stdio.h>
#include <omp.h>
#include <stdlib.h>
#include <string.h>


void MatrixMult(char file1[],char file2[],int T)
{
    char *leftover;

    int row1 = 0;
    int row2 = 0;
    int col1 = 0;
    int col2 = 0;
    int i, j, k;

    double mat1[300][300];
    double mat2[300][300];
    double result[300][300];
    char buff[700];

    omp_set_num_threads(T);

    //Parse the files

    FILE *file1_ptr = fopen(file1, "r");
    fgets(buff, 40, file1_ptr);
    row1 = atoi(&buff[0]);
    col1 = atoi(&buff[1]);
        
    for(i = 0; i < row1; i++) {
	j = 0;
	fgets(buff, sizeof(buff), file1_ptr);
	mat1[i][j] = strtod(buff, &leftover);
	for(j = 1; j < col1; j++) {
	    mat1[i][j] = strtod(leftover, &leftover);
	}
    }
    fclose(file1_ptr);

    FILE *file2_ptr = fopen(file2, "r");
    fgets(buff, 40, file2_ptr);
    row2 = atoi(&buff[0]);
    col2 = atoi(&buff[1]);

    for(i = 0; i < row2; i++) {
	j = 0;
	fgets(buff, sizeof(buff), file2_ptr);
	mat2[i][j] = strtod(buff, &leftover);
	for(j = 1; j < col2; j++) {
	    mat2[i][j] = strtod(leftover, &leftover);
	}
    }
    fclose(file2_ptr);


    //Multiply
#pragma omp parallel shared(mat1) private(i,j,k)
    {
#pragma omp for  schedule(static)
    for(i = 0; i < row1; i++) {
	for(j = 0; j < col2; j++) {
	    int val = 0;
	    for(k = 0; k < col1; k++) {
		val += mat1[i][k]*mat2[k][j];
	    }
	    result[i][j] = val;
	}
    }
    }

    //Printing resulting matrix
    printf("Final Result: \n");
    for(i = 0; i < row1; i++) {
	for(j = 0; j < col2; j++) {
	    printf("%lf ", result[i][j]);
	}
	printf("\n");
    }
}

void main(int argc, char *argv[])
{
    char *file1, *file2;
    file1=argv[1];
    file2=argv[2];
    int T=atoi(argv[3]);

    MatrixMult(file1,file2,T);
}


