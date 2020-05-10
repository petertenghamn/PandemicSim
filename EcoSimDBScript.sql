-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema EcoSimDB
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema EcoSimDB
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `EcoSimDB` DEFAULT CHARACTER SET utf8 ;
USE `EcoSimDB` ;

-- -----------------------------------------------------
-- Table `EcoSimDB`.`records`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `EcoSimDB`.`records` (
  `id` INT NOT NULL,
  `date` DATE NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `EcoSimDB`.`iteration`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `EcoSimDB`.`iteration` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `fox_pop` INT NOT NULL,
  `bunny_pop` INT NOT NULL,
  `grass_pop` INT NOT NULL,
  `records_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_iteration_records_idx` (`records_id` ASC) VISIBLE,
  CONSTRAINT `fk_iteration_records`
    FOREIGN KEY (`records_id`)
    REFERENCES `EcoSimDB`.`records` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
