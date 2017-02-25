# t<- build_gradle[,4]
# t2 <- build_gradle[, 5]
# t3 <- build_gradle[, 6]
# 
# cova <- cov(cbind(t, t2, t3))
# 
# corre <- cor(cbind(t,t2, t3))
# 
# ccfaaa <- ccf(t, t3, 5)
# 

library(readr)
library(ggplot2)

real_projects_evaluation <- read_csv("~/svn/MiningSoftwareProcesses/data/real-projects-evaluation.csv")

mydata <- real_projects_evaluation[c(-1,-2)]
View(mydata)

library(corrplot)
M <- cor(mydata)
corrplot(M, method="circle", type="lower")


plot(mydata)

lmall <- lm(mydata)
coeff <- coef(lmall)
coeff


#artifacts vs strong dependencies
lm1 <- lm(mydata$STRONG_DEPENDENCIES ~ mydata$ARTIFACTS)
coef_lm1 <- coef(lm1)
abline(lm1)
plot(lm1)


#artifacts vs strong dependencies inter containments
lm2 <- lm(mydata$STRONG_INTER_CONTA_DEP ~ mydata$ARTIFACTS)
coef_lm2 <- coef(lm2)
summary(lm2)
abline(lm2)
plot(lm2)



