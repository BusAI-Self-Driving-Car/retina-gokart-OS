//
// Created by maximilien on 22.05.19.
//

#include "TestPacejkaEKF.h"
#include <iostream>
#include <functional>
#include <stdlib.h>
#include <time.h>
#include "WriterEKF.h"


void TestPacejkaEKF::test() {

    EKF::ParameterVec groundTruth;
    groundTruth<< 9, 1, 10 ;
    EKF::ParameterVec guess;
    guess << 9.24, 0.942, 9.93;

    double r = 0.01; // measurement noise
    //double r = static_cast <double> (rand()) / static_cast <double> (RAND_MAX); // measurement noise
    EKF::MeasurementMat measurementNoise = r * EKF::MeasurementMat::Identity();
    double q = 0.01; //process noise
    EKF::ParameterMat processNoise = q * EKF::ParameterMat::Identity();

    // UKF start
    EKF::ParameterVec mean = guess; //using groundTruth
    EKF::ParameterMat variance = EKF::ParameterMat::Identity();
    EKF ekf = EKF(mean, variance);

    std::function<EKF::ParameterVec(EKF::ParameterVec)> predictionFunction
    = [](EKF::ParameterVec parameterVec){
            return parameterVec;
    };

    //for plotting
    Eigen::Matrix<double, NP + 1, NI+1> params;

    for (int i = 0; i<= NI; i++){
        // print
        if(print){
            std::cout << "iteration--------------------------------------- " << i << std::endl;
        }

        // random parameter (side slip) s in range [-1;2];
        double s = 3*static_cast <double> (rand()) / static_cast <double> (RAND_MAX) - 1;
        if(true){
            std::cout << "s: " << s << std::endl;
        }

        // measurement function
        std::function<EKF::MeasurementVec(EKF::ParameterVec)> measureFunction
                = [s](EKF::ParameterVec parameter){

                    double b = parameter(0);
                    double c = parameter(1);
                    double d = parameter(2);

                    double r = d*sin(c*atan(b*s));

                    EKF::MeasurementVec measurementVec;
                    measurementVec << r   ;
                    return measurementVec;
                };
        EKF::MeasurementVec z = measureFunction(groundTruth);

        if(print){
            std::cout << "zMes: " << z << std::endl;
        }

        // EKF Update
        ekf.update(measureFunction,predictionFunction,measurementNoise,processNoise,z);

        //for plotting
        if (writeCSV) {
            Eigen::MatrixXd value(4, 1);
            value << i, ekf.mean(0), ekf.mean(1), ekf.mean(2);
            params.col(i) = value;
        }

    }

    std::cout << "params" << std::endl << params;

    // export for plot
    if(writeCSV) {
        WriterEKF writerEkf;
        writerEkf.writeToCSV("params.csv", params.transpose());
    }



}