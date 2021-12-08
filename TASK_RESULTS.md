# Java Performance Training practical task results

## 1. Measure application performance using files with 100, 500, 5000 records

| File name | File size (kb) | upload time (ms)| displaying time (ms) | CPU usage (percent) | Free RAM (percent) | 
| --- | ----------- | --- | --- | --- | ---|
| survey_results_public_100.csv | 182 | 716 | 265 | 2.7 | 54,562 |
| survey_results_public_500.csv | 878 | 10790 | 629 | 8.3 | 53,758 |
| survey_results_public_5000.csv | 8695 | 839969 | 5893 | 12.1 | 52,4 |

## 2. Identify and describe application performance issues and points for performance optimization

### 2.1. Time-consuming data uploading

#### Description 

Delay is visible even for survey_results_public_500.csv.
So from user perspective it is hard to wait almost 14 minutes for uploading survey_results_public_5000.csv.

#### Points for performance optimization

- Optimize CSVProcessorServiceImpl
- Optimize TransformerFactoryImpl

### 2.2. Time-consuming data displaying

#### Description

Displaying of survey_results_public_5000.csv is noticeable for user, since it takes almost 6 seconds.

#### Points for performance optimization

- Introduce cashing
- Optimize RespondentsServiceImpl.java
- Add paging

## 3. Optimize file uploading

### Simplify transform() in MapToBeanTransformer.

Refactored following part of method transform() 

```
    T bean = beanClass.newInstance();

    for (String boundProperty : bindingConfiguration.getBoundPropertyNames()) {
        Class<Object> boundPropertyClass = bindingConfiguration.getBoundPropertyClass(boundProperty);
        Transformer<Object> propertyTransformer = transformerFactory.getTransformer(boundPropertyClass);
        Object value = propertyTransformer.transform(source, bindingConfiguration.getKeyName(boundProperty));
        PropertyUtils.setProperty(bean, boundProperty, value);
    }
    return bean;
``` 
Results of optimization comparing to initial measurements:

| File name | File size (kb) | upload time reduced on %| displaying time reduced on % |
| --- | ----------- | --- | --- |
| survey_results_public_100.csv | 182 | 75 | 62 |
| survey_results_public_500.csv | 878 | 91 | 79 |
| survey_results_public_5000.csv | 8695 | 92 | 97 |

Details could be found in the table below

| File name | File size (kb) | upload time (ms)| displaying time (ms) |
| --- | ----------- | --- | --- |
| survey_results_public_100.csv | 182 | 176 | 99 |
| survey_results_public_500.csv | 878 | 866 | 130 |
| survey_results_public_5000.csv | 8695 | 66306 | 198 |
