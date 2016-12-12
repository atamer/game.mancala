package game.mancala.controller;

import game.mancala.client.Game;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
class GameController {


    @GetMapping
    public ModelAndView index(Model m) {
        m.addAttribute("size", Game.BOARD_SIZE);
        m.addAttribute("count", Game.STONE_COUNT);
        return new ModelAndView("game");
    }
}
