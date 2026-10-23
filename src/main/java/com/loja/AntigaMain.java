package com.loja;

import com.loja.business.*;
import com.loja.business.interfaces.*;
import com.loja.padraofacade.LojaFacade;
import com.loja.padraofacade.interfaces.ILojaFacade;
import com.loja.repositories.*;
import com.loja.repositories.interfaces.*;
import com.loja.ui.MenuLogin;

public class AntigaMain {
    public static void main(String[] args) {

        // facade
        ILojaFacade facade = new LojaFacade();

        // inicia o sistema
        new MenuLogin(facade).iniciar();
    }
}
