class RegistartionProcessor {
    IOMultiplatformProcessor ioMultiplatformProcessor;
    LocalDataBase localDataBase;
    MySQL mySQLdataBase;

    RegistartionProcessor(IOMultiplatformProcessor ioMultiplatformProcessor, LocalDataBase localDataBase, MySQL mySQLdataBase) {
        this.ioMultiplatformProcessor = ioMultiplatformProcessor;
        this.localDataBase = localDataBase;
        this.mySQLdataBase = mySQLdataBase;
    }

    boolean isRequestEqualRegister(String message) {
        return message.matches("^!register.*");
    }

    boolean isRegisterRequestCorrect(String message) {
        return message.matches("^!register \\w{3,10}");
    }

    void processRequest(Request request) {
        if (!isRequestEqualRegister(request.getMessage())) {
            ioMultiplatformProcessor.sendMes(request.getUser(), "Вы не зарегистрированны.");
            ioMultiplatformProcessor.sendMes(request.getUser(), "Для регистрации введите \"!register ВАШЕ_ИМЯ\" без кавычек.");
            return;
        }
        if (!isRegisterRequestCorrect(request.getMessage())) {
            ioMultiplatformProcessor.sendMes(request.getUser(), "Некорректное имя!");
            ioMultiplatformProcessor.sendMes(request.getUser(), "Имя может содержать от 3 до 10 символов и цифр и не может содержать побелов.");
            return;
        }
        String userName = request.getMessage().split(" ")[1];
        if (localDataBase.isUserNameAlreadyUsed(userName)) {
            ioMultiplatformProcessor.sendMes(request.getUser(), "Имя уже занято, попробуйте другое!");
            return;
        }
        localDataBase.addUser(new User(request.getPlatform(), request.getUserID(), userName));
        mySQLdataBase.insertPlayer(new User(request.getPlatform(), request.getUserID(), userName));
        ioMultiplatformProcessor.sendMes(request.getUser(), userName + ", регистрация прошла успешно.");
    }

    public void putUser(User user){
        localDataBase.addUser(user);
    }
}
